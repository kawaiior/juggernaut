package github.kawaiior.juggernaut.game;

import github.kawaiior.juggernaut.card.GameCard;
import github.kawaiior.juggernaut.card.GameCardInit;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import github.kawaiior.juggernaut.network.packet.SyncCardDataPacket;
import github.kawaiior.juggernaut.network.packet.SyncShieldPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 用于存储游戏中的数据
 */
public class PlayerGameData {
    private static final int RECOVER_SHIELD_TIME = 5000;

    public PlayerGameData(String playerName) {
        this.playerName = playerName;
    }

    @Nullable
    private String playerName;
    private boolean juggernaut = false;
    private int jKillCount = 0;
    private int killCount = 0;
    private int deathCount = 0;
    private float damageAmount = 0;
    private float bearDamage = 0;

    private int cardId = -1;
    private long lastUseSkillTime;
    private long lastUseUltimateSkillTime;
    private long chargingFullTime = -1;
    private int lastCardId = -1;
    private int nextCardId = -1;

    private float shield = 20;
    private float temporaryShield = 0;
    private float maxShield = 20;
    private long lastHurtTime = -1;

    @Nonnull
    public String getPlayerName() {
        if (playerName == null){
            return "-";
        }
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isJuggernaut() {
        return juggernaut;
    }

    public void setJuggernaut(boolean juggernaut) {
        this.juggernaut = juggernaut;
    }

    public void causeDamage(float amount) {
        this.damageAmount += amount;
    }

    public void playerBearDamage(float amount) {
        this.bearDamage += amount;
    }

    public void setjKillCount(int jKillCount) {
        this.jKillCount = jKillCount;
    }

    public void setKillCount(int killCount) {
        this.killCount = killCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public void setDamageAmount(float damageAmount) {
        this.damageAmount = damageAmount;
    }

    public void setBearDamage(float bearDamage) {
        this.bearDamage = bearDamage;
    }

    public int getjKillCount() {
        return jKillCount;
    }

    public void killJuggernaut() {
        this.jKillCount++;
    }

    public void killPlayer() {
        this.killCount++;
    }

    public void playerDeath() {
        this.deathCount++;
    }

    public int getJKillCount() {
        return jKillCount;
    }

    public int getKillCount() {
        return killCount;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public float getDamageAmount() {
        return damageAmount;
    }

    public float getBearDamage() {
        return bearDamage;
    }

    public void resetGameData() {
        this.juggernaut = false;
        this.jKillCount = 0;
        this.killCount = 0;
        this.deathCount = 0;
        this.damageAmount = 0;
        this.bearDamage = 0;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public long getLastUseSkillTime() {
        return lastUseSkillTime;
    }

    public void setLastUseSkillTime(long lastUseSkillTime) {
        this.lastUseSkillTime = lastUseSkillTime;
    }

    public long getLastUseUltimateSkillTime() {
        return lastUseUltimateSkillTime;
    }

    public void setLastUseUltimateSkillTime(long lastUseUltimateSkillTime) {
        this.lastUseUltimateSkillTime = lastUseUltimateSkillTime;
    }

    public long getChargingFullTime() {
        return chargingFullTime;
    }

    public void setChargingFullTime(long chargingFullTime) {
        this.chargingFullTime = chargingFullTime;
    }

    public int getLastCardId() {
        return lastCardId;
    }

    public void setLastCardId(int lastCardId) {
        this.lastCardId = lastCardId;
    }

    public int getNextCardId() {
        return nextCardId;
    }

    public void setNextCardId(int nextCardId) {
        this.nextCardId = nextCardId;
    }

    public void syncCardData(ServerPlayerEntity player){
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                new SyncCardDataPacket(
                        this.getCardId(),
                        this.getLastUseSkillTime(),
                        this.getChargingFullTime(),
                        this.getLastUseUltimateSkillTime(),
                        player.getUniqueID()
                )
        );
    }

    public GameCard getCard(ServerPlayerEntity player) {
        if (cardId == -1){
            if (player == null){
                return null;
            }
            this.cardId = 0;
            this.syncCardData(player);
        }
        return GameCardInit.getGameCardById(cardId);
    }

    public void resetCardData(ServerPlayerEntity player) {
        this.lastUseSkillTime = -1;
        this.chargingFullTime = -1;
        this.lastUseUltimateSkillTime = -1;
        GameCard card = getCard(player);
        if (card != null){
            card.reset(player);
        }
    }

    public boolean skillUsable(ServerPlayerEntity player) {
        long timeNow = System.currentTimeMillis();
        GameCard card = getCard(player);
        if (card.getSkillUseCount() == 1){
            return timeNow > chargingFullTime;
        }

        long firstUseTime = chargingFullTime - (long) card.getSkillCoolDown() * card.getSkillUseCount();
        long canUseTime = firstUseTime + card.getSkillCoolDown();
        return timeNow > canUseTime;
    }

    public boolean ultimateSkillUsable(ServerPlayerEntity player) {
        return System.currentTimeMillis() - lastUseUltimateSkillTime >= getCard(player).getUltimateSkillCoolDown();
    }

    public void afterSkillUse(ServerPlayerEntity player) {
        long time = System.currentTimeMillis();
        lastUseSkillTime = time;
        if (time > chargingFullTime){
            chargingFullTime = time + getCard(player).getSkillCoolDown();
        }else {
            chargingFullTime += getCard(player).getSkillCoolDown();
        }
        syncCardData(player);
        // TODO: 概率播放 Skill 语音
    }

    public void afterUltimateSkillUse(ServerPlayerEntity player) {
        lastUseUltimateSkillTime = System.currentTimeMillis();
        syncCardData(player);
        // TODO: 播放 UltimateSkill 语音
    }

    public float getShield() {
        return shield;
    }

    public void setShield(float shield) {
        this.shield = shield;
    }

    public float getTemporaryShield() {
        return temporaryShield;
    }

    public void setTemporaryShield(float temporaryShield) {
        if (temporaryShield < 0){
            this.temporaryShield = 0;
            return;
        }
        this.temporaryShield = temporaryShield;
    }

    public float getMaxShield() {
        return maxShield;
    }

    public void setMaxShield(float maxShield) {
        this.maxShield = maxShield;
    }

    public long getLastHurtTime() {
        return lastHurtTime;
    }

    public void setLastHurtTime(long lastHurtTime) {
        this.lastHurtTime = lastHurtTime;
    }

    public void shieldTick(ServerPlayerEntity player, long now){
        boolean flag = false;
        // 临时护甲损失
        if (this.getTemporaryShield() > 0){
            this.setTemporaryShield(this.getTemporaryShield() - 1F);
            flag = true;
        }
        // 护甲恢复
        long lastHurtTime = this.getLastHurtTime();
        if (now - lastHurtTime >= RECOVER_SHIELD_TIME && this.getShield() < this.getMaxShield()){
            this.setShield(this.getShield() + 1F);
            flag = true;
        }
        // 网络发包
        if (flag){
            this.syncShieldData(player);
        }
    }

    public void syncShieldData(ServerPlayerEntity player){
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                new SyncShieldPacket(
                        this.getShield(),
                        this.getTemporaryShield(),
                        this.getMaxShield(),
                        player.getUniqueID()
                )
        );
    }
}
