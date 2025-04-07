package github.kawaiior.juggernaut.game;

import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class Constants {
    public static final Random RANDOM = new Random();
    public static final BlockPos GAME_PLAYGROUND_POS = new BlockPos(0, 64, 0);
    public static final int GAME_PLAYGROUND_X_WIDTH = 240;
    public static final int GAME_PLAYGROUND_Z_WIDTH = 240;

    public static final BlockPos GAME_READY_HOME_POS = new BlockPos(-256, 64, -256);
    public static final int GAME_READY_HOME_X_WIDTH = 32;
    public static final int GAME_READY_HOME_Z_WIDTH = 32;

    public static final BlockPos SPAWN_HOME_POS = new BlockPos(-16, 64, -16);
    public static final int SPAWN_HOME_X_WIDTH = 32;
    public static final int SPAWN_HOME_Z_WIDTH = 32;

    public static final BlockPos SPAWN_POS = new BlockPos(-15, 66, -15);
    public static final BlockPos JUGGERNAUT_POS = new BlockPos(128, 66, 128);
    public static final BlockPos READY_HOME_POS = new BlockPos(-255, 66, -255);
    public static final long GAME_MAX_TIME = 1000 * 60 * 2; // 10分钟
    public static final int GAME_PREPARE_TIME = 1000 * 15;  // 60秒
    public static final int GAME_OVER_TIME = 1000 * 15;  // 60秒

    public static final int JUGGERNAUT_MAX_HEALTH = 30;
    public static final int JUGGERNAUT_MAX_SHIELD = 20;
    public static final int PLAYER_MAX_HEALTH = 20;
    public static final int PLAYER_MAX_SHIELD = 20;
}
