package github.kawaiior.juggernaut.card;

import github.kawaiior.juggernaut.Juggernaut;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class GameCard {

    private int cardId;
    private String cardName;
    private int skillUseCount;
    private int skillCoolDown;
    private int ultimateSkillCoolDown;
    private int ultimateSkillEnergy;

    public GameCard(int cardId, String cardName, int skillUseCount, int skillCoolDown, int ultimateSkillCoolDown, int ultimateSkillEnergy) {
        this.cardId = cardId;
        this.cardName = cardName;
        this.skillUseCount = skillUseCount;
        this.skillCoolDown = skillCoolDown;
        this.ultimateSkillCoolDown = ultimateSkillCoolDown;
        this.ultimateSkillEnergy = ultimateSkillEnergy;
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

    public int getUltimateSkillEnergy() {
        return ultimateSkillEnergy;
    }

    public TranslationTextComponent getCardTranslationName(){
        return new TranslationTextComponent("card." + Juggernaut.MOD_ID + "." + cardName);
    }

    public void setUltimateSkillEnergy(int ultimateSkillEnergy) {
        this.ultimateSkillEnergy = ultimateSkillEnergy;
    }

    public abstract void playerUseSkill(PlayerEntity player, PlayerEntity target);

    public abstract void playerUseUltimateSkill(PlayerEntity player, PlayerEntity target);

    public abstract void cardTick(PlayerEntity player);

    public void onPlayerAttack(PlayerEntity player, float damage) {

    }

    public void onPlayerHurt(PlayerEntity player, float damage) {

    }

    public void onPlayerKill(PlayerEntity player) {

    }

}
