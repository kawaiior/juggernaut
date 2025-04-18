package github.kawaiior.juggernaut.game;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import github.kawaiior.juggernaut.network.packet.DeathBoardMsgPacket;
import github.kawaiior.juggernaut.network.packet.GameStatusPacket;
import github.kawaiior.juggernaut.network.packet.SyncAllPlayerGameDataPacket;
import github.kawaiior.juggernaut.network.packet.SyncPlayerGameDataPacket;
import github.kawaiior.juggernaut.util.JuggernautUtil;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
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
    private MinecraftServer server = null;
    private static final Map<ServerPlayerEntity, GameData> GAME_DATA_MAP = new ConcurrentHashMap<>();
    private GameState gameState = GameState.NONE;
    private long gamePrepareTime = -1;
    private long gameStartTime = -1;
    private long gameOverTime = -1;
    private ServerPlayerEntity juggernautPlayer;

    public void gamePrepare() {
        // 所有玩家传送到准备房间
        // 30秒后游戏开始
        // 没有选择角色的玩家将随机选择角色
        this.juggernautPlayer = null;
        this.gameState = GameState.PREPARE;
        this.gamePrepareTime = System.currentTimeMillis();
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(()->null), new GameStatusPacket(1, this.gamePrepareTime));
        GAME_DATA_MAP.forEach((serverPlayer, gameData) -> {
            gameData.getBoardData().reset(serverPlayer);
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

        GAME_DATA_MAP.forEach((serverPlayer, gameData) -> {

            ModifiableAttributeInstance health = serverPlayer.getAttribute(Attributes.MAX_HEALTH);
            if (health != null) {
                health.setBaseValue(Constants.PLAYER_MAX_HEALTH);
            }
            serverPlayer.setHealth(Constants.PLAYER_MAX_HEALTH);

            gameData.getShieldData().maxShield = Constants.PLAYER_MAX_SHIELD;
            gameData.getShieldData().shield = Constants.PLAYER_MAX_SHIELD;
            gameData.getCardData().reset(serverPlayer);
            gameData.getShieldData().syncData(serverPlayer);

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
        NetworkRegistryHandler.INSTANCE.send(
                PacketDistributor.ALL.with(()->null),
                new GameStatusPacket(3, this.gameOverTime)
        );
    }

    public void gameNone(){
        // 游戏结束
        // 所有玩家回到准备房间
        GAME_DATA_MAP.forEach((serverPlayer, gameData) -> {
            JuggernautUtil.teleportPlayerToReadyHome(serverPlayer);
        });
        this.gameState = GameState.NONE;
        NetworkRegistryHandler.INSTANCE.send(
                PacketDistributor.ALL.with(()->null),
                new GameStatusPacket(0, this.gameOverTime)
        );
    }

    // 玩家加入游戏
    public void playerJoinGame(ServerPlayerEntity player, GameData gameData) {
        // 如果游戏已经开始
        if (this.gameState == GameState.START) {
            JuggernautUtil.teleportPlayerToPlayground(player);
        } else if (this.gameState == GameState.PREPARE) {
            JuggernautUtil.teleportPlayerToReadyHome(player);
        }

        // 同步数据
        gameData.getCardData().syncData(player);
        gameData.getShieldData().syncData(player);

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
        gameData.getBoardData().syncData(player);
        // 向此玩家发送所有玩家状态
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncAllPlayerGameDataPacket(GAME_DATA_MAP));
        // TODO 向此玩家发送所有玩家的护甲信息

        // TODO 向此玩家发送所有玩家的card信息

    }

    public void onPlayerHurt(ServerPlayerEntity attacker, ServerPlayerEntity player, float amount) {
        Juggernaut.debug("玩家 " + player.getName().getString() + " 受到伤害 " + amount);
        if (attacker != null) {
            GameData attackerData = GAME_DATA_MAP.get(attacker);
            attackerData.getBoardData().damageAmount += amount;
            // SYNC GAME DATA
            attackerData.getBoardData().syncData(attacker);
        }
        GameData playerData = GAME_DATA_MAP.get(player);
        playerData.getBoardData().bearDamage += amount;
        // SYNC GAME DATA
        playerData.getBoardData().syncData(player);
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

        GameData data = GAME_DATA_MAP.get(player);
        // 更新计分板
        data.getBoardData().deathCount++;
        if (killer != null) {
            GameData killerData = GAME_DATA_MAP.get(killer);
            // 更新计分板
            if (data.getBoardData().juggernaut) {
                killerData.getBoardData().jKillCount++;
                this.juggernautTransfer(player, killer);
            }else {
                killerData.getBoardData().killCount++;
            }
            // SYNC GAME DATA
            killerData.getBoardData().syncData(killer);
        }else {
            if (data.getBoardData().juggernaut){
                Juggernaut.debug("玩家 " + player.getName().getString() + " 死于意外，Juggernaut将被重选");
                JuggernautUtil.removeJuggernautAttribute(player);
                this.juggernautPlayer = null;
                this.choiceJuggernaut();
            }
        }

        // 传送到随机出生点并重置生命值
        JuggernautUtil.teleportPlayerToPlayground(player);
        this.resetPlayerHealthAndShield(player);

        // 重置技能CD
        data.getCardData().chargingFullTime = -1;
        data.getCardData().lastUseSkillTime = -1;

        // SYNC GAME DATA
        data.getCardData().syncData(player);
        data.getBoardData().syncData(player);
    }

    /**
     * 从玩家列表中随机选择一个作为 Juggernaut
     */
    public synchronized void choiceJuggernaut() {
        if (this.juggernautPlayer != null) {
            return;
        }

        // 从GAME_PLAYER_MAP中随机挑选一个Player
        ServerPlayerEntity player = GAME_DATA_MAP.keySet().stream()
                .skip((int) (Math.random() * GAME_DATA_MAP.size())).findFirst().orElse(null);
        if (player == null){
            Juggernaut.debug("没有玩家，无法选择Juggernaut");
            return;
        }

        Juggernaut.debug("玩家 " + player.getName().getString() + " 被选为Juggernaut");
        this.juggernautPlayer = player;
        GameData data = GAME_DATA_MAP.get(player);
        data.getBoardData().juggernaut = true;
        JuggernautUtil.setJuggernautAttribute(player);

        Juggernaut.debug("玩家 " + player.getName().getString() + " 被选为Juggernaut");

        // 网络发包，通知玩家Juggernaut已选择
        String message = "玩家 " + player.getName().getString() + " 被选为Juggernaut";
        new Thread(() -> {
            for (ServerPlayerEntity _player : GAME_DATA_MAP.keySet()) {
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
        GameData juggernautData = GAME_DATA_MAP.get(juggernaut);
        juggernautData.getBoardData().juggernaut = false;
        GameData killerData = GAME_DATA_MAP.get(killer);
        killerData.getBoardData().juggernaut = true;
        this.juggernautPlayer = killer;
        JuggernautUtil.removeJuggernautAttribute(juggernaut);
        JuggernautUtil.setJuggernautAttribute(killer);

        Juggernaut.debug("玩家 " + juggernaut.getName().getString() + " 被玩家 " + killer.getName().getString() + " 杀死，Juggernaut已转移");

        // 网络发包，通知玩家Juggernaut已转移
        String message = "玩家 " + killer.getName().getString() + " 成为新的Juggernaut";
        new Thread(() -> {
            for (ServerPlayerEntity _player : GAME_DATA_MAP.keySet()) {
                _player.sendStatusMessage(new StringTextComponent(message), true);
            }
        }).start();
    }

    private long tickCount = 0;

    private void tickGameState(long timeNow){
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

    public void tick(){
        tickCount++;
        List<ServerPlayerEntity> playersView = this.server.getPlayerList().getPlayers();
        if (tickCount % 20 == 0){
            long now = System.currentTimeMillis();
            for (ServerPlayerEntity player : playersView){
                GameData gameData = this.getPlayerGameData(player);
                if (gameData == null){
                    gameData = new GameData();
                    GAME_DATA_MAP.put(player, gameData);
                    // 玩家加入游戏
                    this.playerJoinGame(player, gameData);
                }else {
                    // 更新护盾
                    gameData.shieldTick(player, now);
                }
            }

            this.tickGameState(now);
        }
    }

    public void onPlayerLoggedIn(ServerPlayerEntity player){

    }

    public void onPlayerLoggedOut(ServerPlayerEntity player){
        if (this.gameState == GameState.START && this.juggernautPlayer.equals(player)){
            // Juggernaut 离开游戏
            this.juggernautPlayer = null;
            this.choiceJuggernaut();
        }
        GAME_DATA_MAP.remove(player);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    private void resetPlayerHealthAndShield(ServerPlayerEntity player){
        player.setHealth(player.getMaxHealth());
        GameData gameData = GAME_DATA_MAP.get(player);
        if (gameData != null) {
            gameData.getShieldData().shield = gameData.getShieldData().maxShield;
            gameData.getShieldData().syncData(player);
        }
    }

    public GameData getPlayerGameData(ServerPlayerEntity player){
        return GAME_DATA_MAP.get(player);
    }

    public Map<ServerPlayerEntity, GameData> getGamePlayerMap() {
        return GAME_DATA_MAP;
    }

    @Nullable
    public ServerPlayerEntity getJuggernautPlayer() {
        return juggernautPlayer;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

}
