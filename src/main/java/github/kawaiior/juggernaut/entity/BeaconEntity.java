package github.kawaiior.juggernaut.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public abstract class BeaconEntity extends Entity {

    private int tickTime = 0;
    private long createTime = -1;
    @Nullable
    protected PlayerEntity owner;
    private boolean init = false;
    private static final DataParameter<Integer> CREATE_TIME = EntityDataManager.createKey(BeaconEntity.class, DataSerializers.VARINT);

    public BeaconEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
    }

//    public void init(){
//        this.dataManager.set(CREATE_TIME, (int) (System.currentTimeMillis() / 1000));
//    }

    protected void tickPerSecond(){

    }

    @Override
    public void tick() {
        if (!init){
            if (this.dataManager.get(CREATE_TIME) == Integer.MAX_VALUE) {
                this.dataManager.set(CREATE_TIME, (int) (System.currentTimeMillis() / 1000));
            }
            createTime = this.dataManager.get(CREATE_TIME) * 1000L;
            init = true;
        }
        if (world.isRemote){
            return;
        }
        if (++tickTime % 20 == 0){
            this.tickPerSecond();
            long now = System.currentTimeMillis();
            if (now - createTime > getMaxLifeTime()){
                this.remove();
                this.removeFromMap();
            }
        }
    }

    @Override
    protected void registerData() {
        this.dataManager.register(CREATE_TIME, Integer.MAX_VALUE);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.dataManager.set(CREATE_TIME, compound.getInt("create_time"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putInt("counter", this.dataManager.get(CREATE_TIME));
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public abstract int getMaxLifeTime();

    public abstract void removeFromMap();

    public abstract void setOwner(PlayerEntity player);

    public void setBeaconDead() {
        this.createTime = -1;
    }

    @Nullable
    public PlayerEntity getOwner() {
        return owner;
    }
}
