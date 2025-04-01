package github.kawaiior.juggernaut.capability.card;

import github.kawaiior.juggernaut.capability.IReplicableCap;
import github.kawaiior.juggernaut.capability.ModCapability;
import github.kawaiior.juggernaut.card.GameCard;
import github.kawaiior.juggernaut.card.GameCardInit;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import github.kawaiior.juggernaut.network.packet.SyncCardDataPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

public class CardPower implements INBTSerializable<CompoundNBT>, IReplicableCap {

    private int cardId;
    private long lastUseSkillTime;
    private long lastUseUltimateSkillTime;
    private float ultimateSkillEnergy;

    public CardPower()  {
        this.cardId = -1;
        this.lastUseSkillTime = 0;
        this.lastUseUltimateSkillTime = 0;
        this.ultimateSkillEnergy = 0;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("card_id", cardId);
        nbt.putLong("last_use_skill_time", lastUseSkillTime);
        nbt.putLong("last_use_ultimate_skill_time", lastUseUltimateSkillTime);
        nbt.putFloat("ultimate_skill_energy", ultimateSkillEnergy);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.cardId = nbt.getInt("card_id");
        this.lastUseSkillTime = nbt.getLong("last_use_skill_time");
        this.lastUseUltimateSkillTime = nbt.getLong("last_use_ultimate_skill_time");
        this.ultimateSkillEnergy = nbt.getFloat("ultimate_skill_energy");
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

    public float getUltimateSkillEnergy() {
        return ultimateSkillEnergy;
    }

    public void setUltimateSkillEnergy(float ultimateSkillEnergy) {
        this.ultimateSkillEnergy = ultimateSkillEnergy;
    }

    @Override
    public void setPower(IReplicableCap iReplicableCap) {
        if (!(iReplicableCap instanceof CardPower)) {
            return;
        }

        CardPower cardPower = (CardPower) iReplicableCap;
        this.cardId = cardPower.getCardId();
        this.lastUseSkillTime = cardPower.getLastUseSkillTime();
        this.lastUseUltimateSkillTime = cardPower.getLastUseUltimateSkillTime();
        this.ultimateSkillEnergy = cardPower.getUltimateSkillEnergy();
    }

    public static void sendCardData(ServerPlayerEntity player) {
        LazyOptional<CardPower> capability = player.getCapability(ModCapability.CARD_POWER);
        capability.ifPresent((power) -> {
            NetworkRegistryHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncCardDataPacket(power.getCardId(), power.getLastUseSkillTime(), power.getLastUseUltimateSkillTime(), power.getUltimateSkillEnergy()));
        });
    }

    public static void sendCardData(ServerPlayerEntity player, CardPower power) {
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncCardDataPacket(power.getCardId(), power.getLastUseSkillTime(), power.getLastUseUltimateSkillTime(), power.getUltimateSkillEnergy()));

    }

    public GameCard getCard() {
        return GameCardInit.getGameCardById(cardId);
    }

    public void reset() {
        this.lastUseSkillTime = -1;
        this.lastUseUltimateSkillTime = -1;
        this.ultimateSkillEnergy = 0;
    }

    public boolean skillUsable() {
        return System.currentTimeMillis() - lastUseSkillTime >= (getCard().getSkillCoolDown() / getCard().getSkillUseCount());
    }

    public boolean ultimateSkillUsable() {
        return System.currentTimeMillis() - lastUseUltimateSkillTime >= getCard().getUltimateSkillCoolDown()
                && ultimateSkillEnergy >= getCard().getUltimateSkillEnergy();
    }

    public void afterSkillUse(ServerPlayerEntity player) {
        long time = System.currentTimeMillis();
        if (time - lastUseSkillTime >= getCard().getSkillCoolDown()){
            lastUseSkillTime = time;
        }else {
            lastUseSkillTime += getCard().getSkillCoolDown() / getCard().getSkillUseCount();
        }
        sendCardData(player);
        // TODO: 概率播放 Skill 语音
    }

    public void afterUltimateSkillUse(ServerPlayerEntity player) {
        lastUseUltimateSkillTime = System.currentTimeMillis();
        ultimateSkillEnergy = 0;
        sendCardData(player);
        // TODO: 播放 UltimateSkill 语音
    }
}
