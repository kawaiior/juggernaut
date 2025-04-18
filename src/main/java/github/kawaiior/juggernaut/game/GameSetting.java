package github.kawaiior.juggernaut.game;

import net.minecraft.util.math.BlockPos;

public class GameSetting {
    public static final BlockPos READY_HOME = new BlockPos(233, 13, -573);
    private BlockPos playerSpawnPos = new BlockPos(0, 64, 0);
    private BlockPos juggernautSpawnPos = new BlockPos(0, 64, 0);

    public BlockPos getPlayerSpawnPos() {
        return playerSpawnPos;
    }

    public void setPlayerSpawnPos(BlockPos playerSpawnPos) {
        this.playerSpawnPos = playerSpawnPos;
    }

    public BlockPos getJuggernautSpawnPos() {
        return juggernautSpawnPos;
    }

    public void setJuggernautSpawnPos(BlockPos juggernautSpawnPos) {
        this.juggernautSpawnPos = juggernautSpawnPos;
    }
}
