package github.kawaiior.juggernaut.game;

import github.kawaiior.juggernaut.Juggernaut;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JuggernautServer {

    private static final JuggernautServer INSTANCE = new JuggernautServer();
    private static final Map<ServerPlayerEntity, PlayerGameData> GAME_PLAYER_MAP = new ConcurrentHashMap<>();

    public static final BlockPos SPAWN_POS = new BlockPos(0, 128, 0);
    public static final BlockPos JUGGERNAUT_POS = new BlockPos(100, 128, 100);
    public static final BlockPos READY_HOME_POS = new BlockPos(200, 128, 200);
    public static final long GAME_MAX_TIME = 1000 * 60 * 10; // 10分钟

    private boolean start = false;
    private long gameStartTime = -1;
    private ServerPlayerEntity juggernautPlayer = null;

    public JuggernautServer() {

    }

    public static JuggernautServer getInstance() {
        return INSTANCE;
    }

    public void gameStart() {
        Juggernaut.debug("====================================");
        Juggernaut.debug("游戏开始");
        Juggernaut.debug("====================================");
        // TODO: 网络发包，通知游戏开始
        this.start = true;
        this.gameStartTime = System.currentTimeMillis();

        this.choiceJuggernaut();
        this.teleportAllPlayer2Ground();
    }

    public void gameOver() {
        Juggernaut.debug("====================================");
        Juggernaut.debug("游戏结束");
        Juggernaut.debug("====================================");
        // TODO: 网络发包，通知游戏结束
        this.start = false;

        // 重置所有玩家的游戏数据
        GAME_PLAYER_MAP.forEach((player, obj) -> {
           obj.reset();
        });
        this.juggernautPlayer = null;
    }

    public void onPlayerHurt(ServerPlayerEntity attacker, ServerPlayerEntity player, float amount) {
        Juggernaut.debug("玩家 " + player.getName().getString() + " 受到伤害 " + amount);
        if (attacker != null) {
            PlayerGameData attackerData = GAME_PLAYER_MAP.get(attacker);
            attackerData.causeDamage(amount);
        }
        PlayerGameData playerData = GAME_PLAYER_MAP.get(player);
        playerData.playerBearDamage(amount);
    }

    public void onPlayerDeath(ServerPlayerEntity killer, ServerPlayerEntity player) {
        Juggernaut.debug("玩家 " + player.getName().getString() + " 死亡");
        // TODO: 网络发包，通知客户端玩家死亡
        PlayerGameData data = GAME_PLAYER_MAP.get(player);
        // 更新计分板
        data.playerDeath();
        if (killer != null) {
            PlayerGameData killerData = GAME_PLAYER_MAP.get(killer);
            // 更新计分板
            killerData.killPlayer();
            if (data.isJuggernaut()) {
                this.juggernautTransfer(player, player);
            }
        }else {
            if (data.isJuggernaut()){
                Juggernaut.debug("玩家 " + player.getName().getString() + " 死于意外，Juggernaut将被重选");
                this.choiceJuggernaut();
            }
        }
        // 传送到随机出生点并重置生命值
        this.teleportPlayerToRandomSpawn(player);
        player.setHealth(player.getMaxHealth());
    }

    private void teleportAllPlayer2ReadyHome() {
        GAME_PLAYER_MAP.forEach((player, obj) -> this.teleportPlayerToReadyHome(player));
    }

    private void teleportAllPlayer2Ground() {
        GAME_PLAYER_MAP.forEach((player, obj) -> {
            if (obj.isJuggernaut()){
                this.teleportJuggernautToRandomSpawn(player);
            }else {
                this.teleportPlayerToRandomSpawn(player);
            }
        });
    }

    public void teleportPlayerToRandomSpawn(ServerPlayerEntity player) {
        player.moveForced(SPAWN_POS.getX(), SPAWN_POS.getY(), SPAWN_POS.getZ());
    }

    public void teleportJuggernautToRandomSpawn(ServerPlayerEntity player) {
        player.moveForced(JUGGERNAUT_POS.getX(), JUGGERNAUT_POS.getY(), JUGGERNAUT_POS.getZ());
    }

    public void teleportPlayerToReadyHome(ServerPlayerEntity player) {
        player.moveForced(READY_HOME_POS.getX(), READY_HOME_POS.getY(), READY_HOME_POS.getZ());
    }

    public void juggernautTransfer(ServerPlayerEntity juggernaut, ServerPlayerEntity killer) {
        PlayerGameData juggernautData = GAME_PLAYER_MAP.get(juggernaut);
        juggernautData.setJuggernaut(false);
        PlayerGameData killerData = GAME_PLAYER_MAP.get(killer);
        killerData.setJuggernaut(true);
        this.juggernautPlayer = killer;
        // TODO: 网络发包，通知玩家Juggernaut已转移
        Juggernaut.debug("玩家 " + juggernaut.getName().getString() + " 被玩家 " + killer.getName().getString() + " 杀死，Juggernaut已转移");
    }

    public void choiceJuggernaut() {
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
        // TODO: 网络发包，通知玩家Juggernaut已选择
    }

    public void updatePlayers(ServerWorld world) {
        // 遍历MAP，如果玩家不在指定维度，则剔除
        GAME_PLAYER_MAP.forEach((player, obj) -> {
            if (player.getEntityWorld().getDimensionKey().getLocation() != world.getDimensionKey().getLocation()) {
                // TODO: 通知客户端有玩家离开游戏
                GAME_PLAYER_MAP.remove(player);
                Juggernaut.debug("玩家 " + player.getName().getString() + " 不在指定维度，已剔除");
            } else if (player.hasDisconnected()) {
                // 玩家掉线
                GAME_PLAYER_MAP.remove(player);
                Juggernaut.debug("玩家 " + player.getName().getString() + " 已断开连接，已剔除");
                if (obj.isJuggernaut()) {
                    this.juggernautPlayer = null;
                }
            } else if (player.isSpectator() || player.isCreative()) {
                // 如果玩家是创造模式或旁观者
                // TODO: 通知客户端有玩家修改游戏模式进而退出游戏
                GAME_PLAYER_MAP.remove(player);
                Juggernaut.debug("玩家 " + player.getName().getString() + " 是创造模式或旁观者，已剔除");
                if (obj.isJuggernaut()) {
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

        if (this.start) {
            if (this.juggernautPlayer == null) {
                this.choiceJuggernaut();
            }
            if (this.juggernautPlayer == null) {
                // 没有选出Juggernaut，游戏提前结束
                this.gameOver();
            }
        }

        Juggernaut.debug("当前游戏人数：" + GAME_PLAYER_MAP.size());
        Juggernaut.debug("当前游戏状态：" + this.start);
    }

    public void playerJoinGame(ServerPlayerEntity player) {
        GAME_PLAYER_MAP.put(player, new PlayerGameData());
        // 如果游戏已经开始
        if (this.start) {
            this.teleportPlayerToRandomSpawn(player);
        }
    }

    public void tick(ServerWorld world){
        long gameTime = world.getGameTime();
        if (gameTime % 20 == 0) {
            this.updatePlayers(world);
        }

        if (!this.start){
            return;
        }

        // TODO: 按照游戏时间发放对应物资

        // 如果时间超过最大时间，则结束游戏
        if (System.currentTimeMillis() - this.gameStartTime > GAME_MAX_TIME) {
            this.gameOver();
        }
    }

    public boolean isStart() {
        return start;
    }
}
