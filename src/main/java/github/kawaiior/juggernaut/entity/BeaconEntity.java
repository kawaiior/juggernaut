package github.kawaiior.juggernaut.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public abstract class BeaconEntity extends ProjectileEntity {

    private int tickTime = 0;

    public BeaconEntity(EntityType<? extends ProjectileEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        if (++tickTime > this.getMaxTicks()){
            this.remove();
            this.removeFromMap();
        }
    }

    @Override
    protected void registerData() {

    }

    @Override
    protected void readAdditional(CompoundNBT compoundNBT) {

    }

    @Override
    protected void writeAdditional(CompoundNBT compoundNBT) {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Nonnull
    public abstract PlayerEntity getOwner();

    public abstract int getMaxTicks();

    public abstract void removeFromMap();
}
