package github.kawaiior.juggernaut.card;

import github.kawaiior.juggernaut.entity.PaintBubbleEntity;
import github.kawaiior.juggernaut.entity.SuperPaintBubbleEntity;
import github.kawaiior.juggernaut.init.EntityTypeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class CardMaddelena extends GameCard{

    @Override
    public void playerUseSkill(ServerPlayerEntity player, ServerPlayerEntity target) {
        PaintBubbleEntity paintBubble = new PaintBubbleEntity(EntityTypeRegistry.PAINT_BUBBLE_ENTITY.get(), player.world);
        paintBubble.setOwner(player);
        paintBubble.setPositionAndUpdate(player.getPosX(), player.getPosYEye(), player.getPosZ());
        paintBubble.setDirectionAndMovement(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
        player.world.addEntity(paintBubble);
    }

    @Override
    public void playerUseUltimateSkill(ServerPlayerEntity player, ServerPlayerEntity target) {
        PaintBubbleEntity paintBubble = new SuperPaintBubbleEntity(EntityTypeRegistry.SUPER_PAINT_BUBBLE_ENTITY.get(), player.world);
        paintBubble.setOwner(player);
        paintBubble.setPositionAndUpdate(player.getPosX(), player.getPosYEye(), player.getPosZ());
        paintBubble.setDirectionAndMovement(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
        player.world.addEntity(paintBubble);
    }

    @Override
    public void cardTick(PlayerEntity player) {

    }
}
