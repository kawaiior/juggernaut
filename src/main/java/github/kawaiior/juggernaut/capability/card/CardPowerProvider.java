package github.kawaiior.juggernaut.capability.card;

import github.kawaiior.juggernaut.capability.ModCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CardPowerProvider implements ICapabilitySerializable<CompoundNBT> {
    private final CardPower instance;

    public CardPowerProvider() {
        this.instance = new CardPower();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapability.CARD_POWER ? LazyOptional.of(()-> this.instance).cast() : LazyOptional.empty();
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
