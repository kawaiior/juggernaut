package github.kawaiior.juggernaut.entity;

import github.kawaiior.juggernaut.init.EntityTypeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReviveBeaconEntity extends BeaconEntity {

    private static final Map<PlayerEntity, ReviveBeaconEntity> map = new ConcurrentHashMap<>();

    private static final int MAX_LIFE = 1000 * 20 * 30; // 30s
    @Nullable
    private PlayerEntity owner;

    public ReviveBeaconEntity(EntityType<? extends ReviveBeaconEntity> type, World world) {
        super(type, world);
    }

    public static ReviveBeaconEntity create(PlayerEntity player) {
        ReviveBeaconEntity entity = new ReviveBeaconEntity(EntityTypeRegistry.REVIVE_BEACON_ENTITY.get(), player.world);
        entity.setOwner(player);
        return entity;
    }

    @Override
    public void setOwner(PlayerEntity owner) {
        this.owner = owner;
        map.put(owner, this);
    }

    @Nullable
    @Override
    public PlayerEntity getOwner() {
        return owner;
    }

    @Override
    public int getMaxLifeTime() {
        return MAX_LIFE;
    }

    @Override
    public void removeFromMap() {
        if (owner!=null) {
            // 判断是否是同一个信标
            if (map.get(owner) == this) {
                map.remove(owner);
            }
        }
    }

    @Nullable
    public static ReviveBeaconEntity getPlayerReviveBeacon(PlayerEntity player) {
        return map.get(player);
    }

}
