package github.kawaiior.juggernaut.card;

import net.minecraft.entity.player.PlayerEntity;

public class CardXin extends GameCard{
    public CardXin(int cardId, String cardName, int skillUseCount, int skillCoolDown, int ultimateSkillCoolDown, int ultimateSkillEnergy) {
        super(cardId, cardName, skillUseCount, skillCoolDown, ultimateSkillCoolDown, ultimateSkillEnergy);
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
