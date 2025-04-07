package github.kawaiior.juggernaut.card;

import github.kawaiior.juggernaut.Juggernaut;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class GameCard {

    private int cardId;
    private String cardName;
    private int skillUseCount;
    private int skillCoolDown;
    private int ultimateSkillCoolDown;
    protected boolean skillNeedTarget;
    protected boolean ultimateSkillNeedTarget;

    public GameCard() {
        this.cardId = -1;
        this.cardName = "undefined";
        this.skillUseCount = 1;
        this.skillCoolDown = 10;
        this.ultimateSkillCoolDown = 60;
        this.skillNeedTarget = false;
        this.ultimateSkillNeedTarget = false;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public int getSkillUseCount() {
        return skillUseCount;
    }

    public void setSkillUseCount(int skillUseCount) {
        this.skillUseCount = skillUseCount;
    }

    public int getSkillCoolDown() {
        return skillCoolDown;
    }

    public void setSkillCoolDown(int skillCoolDown) {
        this.skillCoolDown = skillCoolDown;
    }

    public int getUltimateSkillCoolDown() {
        return ultimateSkillCoolDown;
    }

    public void setUltimateSkillCoolDown(int ultimateSkillCoolDown) {
        this.ultimateSkillCoolDown = ultimateSkillCoolDown;
    }

    public boolean isSkillNeedTarget() {
        return skillNeedTarget;
    }

    public boolean isUltimateSkillNeedTarget() {
        return ultimateSkillNeedTarget;
    }

    public TranslationTextComponent getCardTranslationName(){
        return new TranslationTextComponent("card." + Juggernaut.MOD_ID + "." + cardName);
    }

    public abstract void playerUseSkill(@Nonnull PlayerEntity player, @Nullable PlayerEntity target);

    public abstract void playerUseUltimateSkill(@Nonnull PlayerEntity player, @Nullable PlayerEntity target);

    public abstract void cardTick(@Nonnull PlayerEntity player);

    public boolean onPlayerDeath(@Nonnull PlayerEntity player) {
        return false;
    }

    public void reset(PlayerEntity player){

    }
}
