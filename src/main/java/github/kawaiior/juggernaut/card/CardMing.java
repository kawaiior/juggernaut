package github.kawaiior.juggernaut.card;

import github.kawaiior.juggernaut.entity.ElectricBallEntity;
import github.kawaiior.juggernaut.entity.PaintBubbleEntity;
import github.kawaiior.juggernaut.game.GameServer;
import github.kawaiior.juggernaut.game.PlayerGameData;
import github.kawaiior.juggernaut.init.EntityTypeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class CardMing extends GameCard{

    @Override
    public void playerUseSkill(ServerPlayerEntity player, ServerPlayerEntity target) {
        ElectricBallEntity electricBallEntity = new ElectricBallEntity(EntityTypeRegistry.ELECTRIC_BALL_ENTITY.get(), player.world);
        electricBallEntity.setOwner(player);
        electricBallEntity.setPositionAndUpdate(player.getPosX(), player.getPosYEye(), player.getPosZ());
        electricBallEntity.setDirectionAndMovement(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
        player.world.addEntity(electricBallEntity);
    }

    @Override
    public void playerUseUltimateSkill(ServerPlayerEntity player, ServerPlayerEntity target) {
        // 50点临时护甲
        PlayerGameData playerGameData = GameServer.getInstance().getPlayerGameData(player);
        playerGameData.setTemporaryShield(playerGameData.getTemporaryShield() + 50);
    }

    @Override
    public void cardTick(PlayerEntity player) {

    }
}
