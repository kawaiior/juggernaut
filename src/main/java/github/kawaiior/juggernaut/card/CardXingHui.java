package github.kawaiior.juggernaut.card;

import github.kawaiior.juggernaut.game.GameServer;
import github.kawaiior.juggernaut.game.PlayerGameData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class CardXingHui extends GameCard{

    public CardXingHui() {
        this.skillNeedTarget = true;
        this.ultimateSkillNeedTarget = true;
    }

    @Override
    public void playerUseSkill(ServerPlayerEntity player, ServerPlayerEntity target) {
        // 10点临时护甲
        PlayerGameData playerGameData = GameServer.getInstance().getPlayerGameData(player);
        PlayerGameData targetGameData = GameServer.getInstance().getPlayerGameData(target);

        playerGameData.setTemporaryShield(playerGameData.getTemporaryShield() + 10);
        targetGameData.setTemporaryShield(targetGameData.getTemporaryShield() + 10);
    }

    @Override
    public void playerUseUltimateSkill(ServerPlayerEntity player, ServerPlayerEntity target) {
        if (target == null){
            return;
        }
        player.moveForced(target.getPositionVec());

        // 30点临时护甲
        PlayerGameData playerGameData = GameServer.getInstance().getPlayerGameData(player);
        PlayerGameData targetGameData = GameServer.getInstance().getPlayerGameData(target);

        playerGameData.setTemporaryShield(playerGameData.getTemporaryShield() + 30);
        targetGameData.setTemporaryShield(targetGameData.getTemporaryShield() + 30);
    }

    @Override
    public void cardTick(PlayerEntity player) {

    }


}
