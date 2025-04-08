package github.kawaiior.juggernaut.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WatchBeaconEntity extends BeaconEntity{

    private static final Map<PlayerEntity, WatchBeaconEntity> map = new ConcurrentHashMap<>();
    private static final int MAX_LIFE = 1000 * 15; // 15s

    public WatchBeaconEntity(EntityType<WatchBeaconEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void tickPerSecond() {
        if (owner!=null && owner.getDistance(this) <= 5F){
            owner.addPotionEffect(new EffectInstance(Effects.STRENGTH, 100, 0));
            owner.addPotionEffect(new EffectInstance(Effects.REGENERATION, 100, 0));
        }
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

    @Override
    public void setOwner(PlayerEntity player) {
        this.owner = player;
        map.put(player, this);
    }
}
