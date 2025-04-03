package github.kawaiior.juggernaut.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class ReviveBeaconEntity extends BeaconEntity {

    private static final int MAX_LIFE = 20 * 30; // 30s
    private PlayerEntity owner;

    public ReviveBeaconEntity(EntityType<? extends ProjectileEntity> type, World world) {
        super(type, world);
    }

    public void setOwner(PlayerEntity owner) {
        this.owner = owner;
    }

    @Nonnull
    @Override
    public PlayerEntity getOwner() {
        return owner;
    }

    @Override
    public int getMaxTicks() {
        return MAX_LIFE;
    }

    @Override
    public void removeFromMap() {

    }

}
