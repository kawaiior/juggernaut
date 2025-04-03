package github.kawaiior.juggernaut.card;

import net.minecraft.entity.player.PlayerEntity;

public class CardXingHui extends GameCard{

    public CardXingHui() {
        this.skillNeedTarget = true;
        this.ultimateSkillNeedTarget = true;
    }

    @Override
    public void playerUseSkill(PlayerEntity player, PlayerEntity target) {

    }

    @Override
    public void playerUseUltimateSkill(PlayerEntity player, PlayerEntity target) {

    }

    @Override
    public void cardTick(PlayerEntity player) {

    }


}
