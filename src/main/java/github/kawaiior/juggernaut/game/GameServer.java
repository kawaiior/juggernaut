package github.kawaiior.juggernaut.game;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.card.GameCard;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import github.kawaiior.juggernaut.network.packet.DeathBoardMsgPacket;
import github.kawaiior.juggernaut.network.packet.GameStatusPacket;
import github.kawaiior.juggernaut.network.packet.SyncAllPlayerGameDataPacket;
import github.kawaiior.juggernaut.network.packet.SyncPlayerGameDataPacket;
import github.kawaiior.juggernaut.util.JuggernautUtil;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {

    public enum GameState {
        NONE,
        PREPARE,
        START,
        OVER
    }

    private static final GameServer INSTANCE = new GameServer();
    public static GameServer getInstance() {
        return INSTANCE;
    }

    private static final Map<ServerPlayerEntity, PlayerGameData> GAME_PLAYER_MAP = new ConcurrentHashMap<>();
    private GameState gameState = GameState.NONE;
    private long gamePrepareTime = -1;
    private long gameStartTime = -1;
    private long gameOverTime = -1;
    private ServerPlayerEntity juggernautPlayer;

    public void gamePrepare() {
        // 所有玩家传送到准备房间
        // 30秒后游戏开始
        // 没有选择角色的玩家将随机选择角色
        this.gameState = GameState.PREPARE;
        this.gamePrepareTime = System.currentTimeMillis();
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(()->null), new GameStatusPacket(1, this.gamePrepareTime));
        GAME_PLAYER_MAP.forEach((serverPlayer, gameData) -> {
            JuggernautUtil.teleportPlayerToReadyHome(serverPlayer);
        });
    }

    public void gameStart() {
        // 所有玩家传送到游戏房间
        // 随机选择一位玩家作为Juggernaut，并传送至游戏房间中央
        // 游戏时间达到10分钟后或有玩家的击杀数达到30次时，游戏结束
        this.gameState = GameState.START;
        this.gameStartTime = System.currentTimeMillis();
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(()->null), new GameStatusPacket(2, this.gameStartTime));
        this.juggernautPlayer = null;

        GAME_PLAYER_MAP.forEach((serverPlayer, gameData) -> {

            GameCard card = gameData.getCard(serverPlayer);
            card.reset(serverPlayer);
            gameData.setShield(gameData.getMaxShield());
            gameData.resetGameData();
            gameData.resetCardData(serverPlayer);
            gameData.syncCardData(serverPlayer);
            gameData.syncShieldData(serverPlayer);

            JuggernautUtil.teleportPlayerToPlayground(serverPlayer);
        });

        this.choiceJuggernaut();
        JuggernautUtil.teleportJuggernautToPlayground(this.juggernautPlayer);
    }

    public void gameOver(){
        // 游戏结束后玩家将无法造成伤害
        // 并将在30秒后传送到准备房间
        this.gameState = GameState.OVER;
        this.gameOverTime = System.currentTimeMillis();
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(()->null), new GameStatusPacket(3, this.gameOverTime));
    }

    public void gameNone(){
        // 游戏结束
        // 所有玩家回到准备房间
        GAME_PLAYER_MAP.forEach((serverPlayer, gameData) -> {
            JuggernautUtil.teleportPlayerToReadyHome(serverPlayer);
        });
        this.gameState = GameState.NONE;
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(()->null), new GameStatusPacket(0, this.gameOverTime));
    }

    // 玩家加入游戏
    public void playerJoinGame(ServerPlayerEntity player) {
        PlayerGameData gameData = new PlayerGameData(player.getName().getString());
        GAME_PLAYER_MAP.put(player, gameData);
        // 如果游戏已经开始
        if (this.gameState == GameState.START) {
            // TODO: 设置玩家血量
            JuggernautUtil.teleportPlayerToPlayground(player);
        } else if (this.gameState == GameState.PREPARE) {
            JuggernautUtil.teleportPlayerToReadyHome(player);
        }

        // 同步数据
        gameData.syncCardData(player);
        gameData.syncShieldData(player);
        ModifiableAttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(Constants.PLAYER_MAX_HEALTH);
        }
        player.setHealth(Constants.PLAYER_MAX_HEALTH);

        int status = 0;
        long time = -1;
        if (this.gameState == GameState.START){
            status = 2;
            time = gameStartTime;
        } else if (this.gameState == GameState.PREPARE) {
            status = 1;
            time = gamePrepareTime;
        }

        // 发送游戏状态
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                new GameStatusPacket(status, time));
        // 向所有玩家发送此玩家的状态
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                new SyncPlayerGameDataPacket(player.getUniqueID(), gameData));
        // 向此玩家发送所有玩家状态
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncAllPlayerGameDataPacket(GAME_PLAYER_MAP));
    }

    public void onPlayerHurt(ServerPlayerEntity attacker, ServerPlayerEntity player, float amount) {
        Juggernaut.debug("玩家 " + player.getName().getString() + " 受到伤害 " + amount);
        if (attacker != null) {
            PlayerGameData attackerData = GAME_PLAYER_MAP.get(attacker);
            attackerData.causeDamage(amount);

            // SYNC GAME DATA
            NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                    new SyncPlayerGameDataPacket(attacker.getUniqueID(), attackerData));
        }
        PlayerGameData playerData = GAME_PLAYER_MAP.get(player);
        playerData.playerBearDamage(amount);

        // SYNC GAME DATA
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                new SyncPlayerGameDataPacket(player.getUniqueID(), playerData));
    }

    public void onPlayerDeath(ServerPlayerEntity killer, ServerPlayerEntity player) {
        Juggernaut.debug("玩家 " + player.getName().getString() + " 死亡");

        // 网络发包，通知客户端玩家死亡
        DeathBoardMsgPacket packet;
        if (killer == null){
            packet = new DeathBoardMsgPacket("玩家 " + player.getName().getString() + " 死于意外");
        }else {
            packet = new DeathBoardMsgPacket("玩家 " + player.getName().getString() + " 被 " + killer.getName().getString() + " 击杀");
        }
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null), packet);

        PlayerGameData data = GAME_PLAYER_MAP.get(player);
        // 更新计分板
        data.playerDeath();
        if (killer != null) {
            PlayerGameData killerData = GAME_PLAYER_MAP.get(killer);
            // 更新计分板
            if (data.isJuggernaut()) {
                killerData.killJuggernaut();
                this.juggernautTransfer(player, killer);
            }else {
                killerData.killPlayer();
            }
            // SYNC GAME DATA
            NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                    new SyncPlayerGameDataPacket(killer.getUniqueID(), killerData));
        }else {
            if (data.isJuggernaut()){
                Juggernaut.debug("玩家 " + player.getName().getString() + " 死于意外，Juggernaut将被重选");
                this.juggernautPlayer = null;
                this.choiceJuggernaut();
            }
        }

        // 传送到随机出生点并重置生命值
        JuggernautUtil.teleportPlayerToPlayground(player);
        this.resetPlayerHealthAndShield(player);

        // 重置技能CD
        data.setChargingFullTime(-1);
        data.setLastUseSkillTime(-1);

        // SYNC GAME DATA
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                new SyncPlayerGameDataPacket(player.getUniqueID(), data));
    }

    /**
     * 从玩家列表中随机选择一个作为 Juggernaut
     */
    public synchronized void choiceJuggernaut() {
        if (this.juggernautPlayer != null) {
            return;
        }

        // 从GAME_PLAYER_MAP中随机挑选一个Player
        ServerPlayerEntity player = GAME_PLAYER_MAP.keySet().stream()
                .skip((int) (Math.random() * GAME_PLAYER_MAP.size())).findFirst().orElse(null);
        if (player == null){
            Juggernaut.debug("没有玩家，无法选择Juggernaut");
            return;
        }

        Juggernaut.debug("玩家 " + player.getName().getString() + " 被选为Juggernaut");
        this.juggernautPlayer = player;
        PlayerGameData data = GAME_PLAYER_MAP.get(player);
        data.setJuggernaut(true);
        JuggernautUtil.setJuggernautAttribute(player);

        Juggernaut.debug("玩家 " + player.getName().getString() + " 被选为Juggernaut");

        // 网络发包，通知玩家Juggernaut已选择
        String message = "玩家 " + player.getName().getString() + " 被选为Juggernaut";
        new Thread(() -> {
            for (ServerPlayerEntity _player : GAME_PLAYER_MAP.keySet()) {
                if (_player.equals(player)){
                    _player.sendStatusMessage(new StringTextComponent("你被选为Juggernaut"), true);
                    continue;
                }
                _player.sendStatusMessage(new StringTextComponent(message), true);
            }
        }).start();
    }

    /**
     * 将 Juggernaut 身份转移到击杀者身上
     */
    public void juggernautTransfer(ServerPlayerEntity juggernaut, ServerPlayerEntity killer) {
        PlayerGameData juggernautData = GAME_PLAYER_MAP.get(juggernaut);
        juggernautData.setJuggernaut(false);
        PlayerGameData killerData = GAME_PLAYER_MAP.get(killer);
        killerData.setJuggernaut(true);
        this.juggernautPlayer = killer;
        JuggernautUtil.removeJuggernautAttribute(juggernaut);
        JuggernautUtil.setJuggernautAttribute(killer);

        Juggernaut.debug("玩家 " + juggernaut.getName().getString() + " 被玩家 " + killer.getName().getString() + " 杀死，Juggernaut已转移");

        // 网络发包，通知玩家Juggernaut已转移
        String message = "玩家 " + killer.getName().getString() + " 成为新的Juggernaut";
        new Thread(() -> {
            for (ServerPlayerEntity _player : GAME_PLAYER_MAP.keySet()) {
                _player.sendStatusMessage(new StringTextComponent(message), true);
            }
        }).start();
    }

    // 遍历玩家列表
    public void updateWorldPlayers(ServerWorld world){
        // 遍历MAP，如果玩家不在指定维度，则剔除
        GAME_PLAYER_MAP.forEach((player, gameData) -> {
            if (player.getEntityWorld().getDimensionKey().getLocation() != world.getDimensionKey().getLocation()) {
                // TODO: 通知客户端有玩家离开游戏
                GAME_PLAYER_MAP.remove(player);
                if (gameData.isJuggernaut()) {
                    this.juggernautPlayer = null;
                }
                Juggernaut.debug("玩家 " + player.getName().getString() + " 不在指定维度，已剔除");
            } else if (player.hasDisconnected()) {
                // 玩家掉线
                GAME_PLAYER_MAP.remove(player);
                Juggernaut.debug("玩家 " + player.getName().getString() + " 已断开连接，已剔除");
                if (gameData.isJuggernaut()) {
                    this.juggernautPlayer = null;
                }
            } else if (player.isSpectator() || player.isCreative()) {
                // 如果玩家是创造模式或旁观者
                // TODO: 通知客户端有玩家修改游戏模式进而退出游戏
                GAME_PLAYER_MAP.remove(player);
                Juggernaut.debug("玩家 " + player.getName().getString() + " 是创造模式或旁观者，已剔除");
                if (gameData.isJuggernaut()) {
                    this.juggernautPlayer = null;
                }
            }
        });

        // 遍历世界玩家，如果不在MAP中，则加入
        List<ServerPlayerEntity> players = world.getPlayers();
        players.forEach(player -> {
            if (!GAME_PLAYER_MAP.containsKey(player)) {
                if (player.isCreative() || player.isSpectator()){
                    Juggernaut.debug("玩家 " + player.getName().getString() + " 是创造模式或旁观者，不参与游戏");
                }else {
                    this.playerJoinGame(player);
                    Juggernaut.debug("玩家 " + player.getName().getString() + " 已加入游戏");
                }
            }
        });

        if (this.gameState == GameState.START) {
            if (this.juggernautPlayer == null) {
                this.choiceJuggernaut();
            }
            if (this.juggernautPlayer == null) {
                // 没有选出Juggernaut，游戏提前结束
                this.gameOver();
            }
        }
    }

    /**
     * 服务端游戏帧
     */
    public void tick(ServerWorld world){
        long gameTime = world.getGameTime();
        if (gameTime % 20 != 0){
            return;
        }

        long timeNow = System.currentTimeMillis();

        // TODO: 按照游戏时间发放对应物资

        // 遍历一遍玩家列表，更新玩家状态
        this.updateWorldPlayers(world);

        if (this.gameState == GameState.NONE) {
            return;
        }

        if (this.gameState == GameState.PREPARE) {
            if (timeNow - this.gamePrepareTime >= Constants.GAME_PREPARE_TIME) {
                this.gameStart();
            }
        } else if (this.gameState == GameState.START) {
            if (timeNow - this.gameStartTime >= Constants.GAME_MAX_TIME) {
                this.gameOver();
            }
        } else if (this.gameState == GameState.OVER) {
            if (timeNow - this.gameOverTime >= Constants.GAME_OVER_TIME) {
                this.gameNone();
            }
        }

    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    private void resetPlayerHealthAndShield(ServerPlayerEntity player){
        player.setHealth(player.getMaxHealth());
        PlayerGameData gameData = GAME_PLAYER_MAP.get(player);
        if (gameData != null) {
            gameData.setShield(gameData.getMaxShield());
            gameData.syncShieldData(player);
        }
    }

    public PlayerGameData getPlayerGameData(ServerPlayerEntity player){
        return GAME_PLAYER_MAP.get(player);
    }
}
