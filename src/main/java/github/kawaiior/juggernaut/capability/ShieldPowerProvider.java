package github.kawaiior.juggernaut.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShieldPowerProvider implements ICapabilitySerializable<CompoundNBT> {
    private final ShieldPower instance;

    public ShieldPowerProvider() {
        this.instance = new ShieldPower();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapability.SHIELD_POWER ? LazyOptional.of(()-> this.instance).cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.instance.deserializeNBT(nbt);
    }
}
