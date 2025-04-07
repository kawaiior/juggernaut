package github.kawaiior.juggernaut.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public abstract class BeaconEntity extends Entity {

    private int tickTime = 0;
    private long createTime = System.currentTimeMillis();

    public BeaconEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        if (world.isRemote){
            return;
        }
        if (++tickTime % 20 == 0){
            long now = System.currentTimeMillis();
            if (now - createTime > getMaxLifeTime()){
                this.remove();
                this.removeFromMap();
            }
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

    @Nullable
    public abstract PlayerEntity getOwner();

    public abstract int getMaxLifeTime();

    public abstract void removeFromMap();

    public abstract void setOwner(PlayerEntity player);

    public void setBeaconDead() {
        this.tickTime = this.getMaxLifeTime();
    }
}
