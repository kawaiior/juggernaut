package github.kawaiior.juggernaut.entity;

import github.kawaiior.juggernaut.game.GameServer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SuperWatchBeaconEntity extends BeaconEntity{

    private static final Map<PlayerEntity, SuperWatchBeaconEntity> map = new ConcurrentHashMap<>();
    private static final int MAX_LIFE = 1000 * 30; // 30s

    public SuperWatchBeaconEntity(EntityType<SuperWatchBeaconEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void tickPerSecond() {
        if (owner!=null && owner.getDistance(this) <= 25F){
            owner.addPotionEffect(new EffectInstance(Effects.STRENGTH, 100, 0));
            owner.addPotionEffect(new EffectInstance(Effects.REGENERATION, 100, 0));
        }
        // 判断juggernaut是否在范围内
        PlayerEntity juggernaut = GameServer.getInstance().getJuggernautPlayer();
        if (juggernaut != null && this.getDistance(juggernaut) <= 25F){
            juggernaut.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 100, 0));
            juggernaut.addPotionEffect(new EffectInstance(Effects.GLOWING, 100, 0));
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
