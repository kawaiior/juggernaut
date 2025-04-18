package github.kawaiior.juggernaut.card;

import github.kawaiior.juggernaut.game.GameData;
import github.kawaiior.juggernaut.game.GameServer;
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
        GameData playerGameData = GameServer.getInstance().getPlayerGameData(player);
        GameData targetGameData = GameServer.getInstance().getPlayerGameData(target);

        playerGameData.getShieldData().temporaryShield += 10;
        targetGameData.getShieldData().temporaryShield += 10;
    }

    @Override
    public void playerUseUltimateSkill(ServerPlayerEntity player, ServerPlayerEntity target) {
        if (target == null){
            return;
        }
        player.moveForced(target.getPositionVec());

        // 30点临时护甲
        GameData playerGameData = GameServer.getInstance().getPlayerGameData(player);
        GameData targetGameData = GameServer.getInstance().getPlayerGameData(target);

        playerGameData.getShieldData().temporaryShield += 30;
        targetGameData.getShieldData().temporaryShield += 30;
    }

    @Override
    public void cardTick(PlayerEntity player) {

    }


}
