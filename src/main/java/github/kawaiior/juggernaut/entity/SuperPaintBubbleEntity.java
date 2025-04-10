package github.kawaiior.juggernaut.entity;

import github.kawaiior.juggernaut.game.GameServer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class SuperPaintBubbleEntity extends PaintBubbleEntity{
    public SuperPaintBubbleEntity(EntityType<PaintBubbleEntity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void tickAfterHit() {
        if (this.world.isRemote){
            return;
        }
        // 判断juggernaut是否在范围内
        PlayerEntity juggernaut = GameServer.getInstance().getJuggernautPlayer();
        if (juggernaut != null && this.getDistance(juggernaut) <= 15F){
            juggernaut.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 1));
            // TODO 添加易伤
        }
    }

    @Override
    public long getMaxTimeAfterHit() {
        return 1000 * 30;  // 30s
    }
}
