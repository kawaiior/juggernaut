package github.kawaiior.juggernaut.game;

import github.kawaiior.juggernaut.card.GameCard;
import github.kawaiior.juggernaut.card.GameCardInit;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import github.kawaiior.juggernaut.network.packet.SyncCardDataPacket;
import github.kawaiior.juggernaut.network.packet.SyncPlayerGameDataPacket;
import github.kawaiior.juggernaut.network.packet.SyncShieldPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;


public class GameData {

    public static class BoardData{
        public boolean juggernaut = false;
        public int jKillCount = 0;
        public int killCount = 0;
        public int deathCount = 0;
        public float damageAmount = 0;
        public float bearDamage = 0;
        public long lastHurtTime = -1;
        public void syncData(ServerPlayerEntity player){
            NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                    new SyncPlayerGameDataPacket(player.getUniqueID(), player.getName().getString(), this));
        }
        public void reset(ServerPlayerEntity player){
            juggernaut = false;
            jKillCount = 0;
            killCount = 0;
            deathCount = 0;
            damageAmount = 0;
            bearDamage = 0;
            lastHurtTime = -1;

            this.syncData(player);
        }
    }

    public static class CardData{
        public int cardId = -1;
        public long lastUseSkillTime = -1;
        public long lastUseUltimateSkillTime = -1;
        public long chargingFullTime = -1;
        public int lastCardId = -1;
        public int nextCardId = -1;
        public void syncData(ServerPlayerEntity player){
            NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                    new SyncCardDataPacket(
                            this.cardId,
                            this.lastUseSkillTime,
                            this.chargingFullTime,
                            this.lastUseUltimateSkillTime,
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
                this.syncData(player);
            }
            return GameCardInit.getGameCardById(cardId);
        }
        public void reset(ServerPlayerEntity player){
            this.lastUseSkillTime = -1;
            this.chargingFullTime = -1;
            this.lastUseUltimateSkillTime = -1;
            GameCard card = getCard(player);
            if (card != null){
                card.reset(player);
            }
            this.syncData(player);
        }
    }

    public static class ShieldData{
        public float shield = 20;
        public float temporaryShield = 0;
        public float maxShield = 20;

        public void syncData(ServerPlayerEntity player){
            NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                    new SyncShieldPacket(
                            this.shield,
                            this.temporaryShield,
                            this.maxShield,
                            player.getUniqueID()
                    )
            );
        }
    }

    public String playerName = "NULL";
    public boolean play = true;
    private final BoardData boardData = new BoardData();
    private final CardData cardData = new CardData();
    private final ShieldData shieldData = new ShieldData();

    public BoardData getBoardData() {
        return boardData;
    }

    public CardData getCardData() {
        return cardData;
    }

    public ShieldData getShieldData() {
        return shieldData;
    }
    private static final int RECOVER_SHIELD_TIME = 5000;
    public void shieldTick(ServerPlayerEntity player, long now){
        boolean flag = false;
        // 临时护甲损失
        if (this.getShieldData().temporaryShield > 0){
            this.getShieldData().temporaryShield--;
            if (this.getShieldData().temporaryShield < 0){
                this.getShieldData().temporaryShield = 0;
            }
            flag = true;
        }
        // 护甲恢复
        long lastHurtTime = this.getBoardData().lastHurtTime;
        if (now - lastHurtTime >= RECOVER_SHIELD_TIME && this.getShieldData().shield < this.getShieldData().maxShield){
            this.getShieldData().shield++;
            if (this.getShieldData().shield > this.getShieldData().maxShield){
                this.getShieldData().shield = this.getShieldData().maxShield;
            }
            flag = true;
        }
        // 网络发包
        if (flag){
            this.getShieldData().syncData(player);
        }
    }

    public boolean skillUsable(ServerPlayerEntity player) {
        long timeNow = System.currentTimeMillis();
        GameCard card = this.cardData.getCard(player);
        if (card.getSkillUseCount() == 1){
            return timeNow > this.cardData.chargingFullTime;
        }

        long firstUseTime = this.cardData.chargingFullTime - (long) card.getSkillCoolDown() * card.getSkillUseCount();
        long canUseTime = firstUseTime + card.getSkillCoolDown();
        return timeNow > canUseTime;
    }

    public boolean ultimateSkillUsable(ServerPlayerEntity player) {
        return System.currentTimeMillis() - this.cardData.lastUseUltimateSkillTime >= this.cardData.getCard(player).getUltimateSkillCoolDown();
    }

    public void afterSkillUse(ServerPlayerEntity player) {
        long time = System.currentTimeMillis();
        this.cardData.lastUseSkillTime = time;
        if (time > this.cardData.chargingFullTime){
            this.cardData.chargingFullTime = time + this.cardData.getCard(player).getSkillCoolDown();
        }else {
            this.cardData.chargingFullTime += this.cardData.getCard(player).getSkillCoolDown();
        }
        this.cardData.syncData(player);
    }

    public void afterUltimateSkillUse(ServerPlayerEntity player) {
        this.cardData.lastUseUltimateSkillTime = System.currentTimeMillis();
        this.cardData.syncData(player);
    }
}
