package github.kawaiior.juggernaut.capability.shield;

import github.kawaiior.juggernaut.capability.IReplicableCap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShieldPower implements INBTSerializable<CompoundNBT>, IReplicableCap {

    public static class PlayerShieldData{
        public final float shield;
        public final float maxShield;
        public final UUID playerUUID;

        public PlayerShieldData(float shield, float maxShield, UUID playerUUID) {
            this.shield = shield;
            this.maxShield = maxShield;
            this.playerUUID = playerUUID;
        }
    }

    // 这个MAP用于渲染
    public static final Map<UUID, PlayerShieldData> SHIELD_DATA = new HashMap<>();

    private float playerShield;
    private float playerMaxShield;

    public ShieldPower() {
        this.playerShield = 20F;
        this.playerMaxShield = 20F;
    }

    public float getPlayerShield() {
        return playerShield;
    }

    public void setPlayerShield(float playerShield) {
        // 允许护甲溢出
        if (playerShield < 0) {
            this.playerShield = 0;
            return;
        }
        this.playerShield = playerShield;
    }

    public float getPlayerMaxShield() {
        return playerMaxShield;
    }

    public void setPlayerMaxShield(float playerMaxShield) {
        this.playerMaxShield = playerMaxShield;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putFloat("shield", playerShield);
        nbt.putFloat("shield_max", playerMaxShield);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.playerShield = nbt.getFloat("shield");
        this.playerMaxShield = nbt.getFloat("shield_max");
    }

    @Override
    public void setPower(IReplicableCap iReplicableCap) {
        if (!(iReplicableCap instanceof ShieldPower)) {
            return;
        }

        ShieldPower shieldPower = (ShieldPower) iReplicableCap;
        this.playerShield = shieldPower.getPlayerShield();
        this.playerMaxShield = shieldPower.getPlayerMaxShield();
    }
}
