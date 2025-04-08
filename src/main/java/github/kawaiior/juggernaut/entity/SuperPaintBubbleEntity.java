package github.kawaiior.juggernaut.entity;

import github.kawaiior.juggernaut.game.GameServer;
import net.minecraft.entity.EntityType;
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
        // 遍历游戏内的玩家
        GameServer.getInstance().getGamePlayerMap().forEach((player, gameData) -> {
            // 如果不是owner 并且距离小于3 则施加缓慢2效果
            if (this.getDistance(player) <= 15F && !player.equals(this.getOwner())) {
                player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 1));
                // TODO 添加易伤
            }
        });
    }

    @Override
    public long getMaxTimeAfterHit() {
        return 1000 * 30;  // 30s
    }
}
