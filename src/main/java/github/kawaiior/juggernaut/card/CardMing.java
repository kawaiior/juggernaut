package github.kawaiior.juggernaut.card;

import github.kawaiior.juggernaut.game.GameServer;
import github.kawaiior.juggernaut.game.PlayerGameData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class CardMing extends GameCard{

    @Override
    public void playerUseSkill(ServerPlayerEntity player, ServerPlayerEntity target) {

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
