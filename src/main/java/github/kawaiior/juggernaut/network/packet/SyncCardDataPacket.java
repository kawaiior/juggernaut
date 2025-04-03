package github.kawaiior.juggernaut.network.packet;


import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.card.GameCard;
import github.kawaiior.juggernaut.card.GameCardInit;
import github.kawaiior.juggernaut.game.JuggernautClient;
import github.kawaiior.juggernaut.game.PlayerGameData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncCardDataPacket {

    private final int cardId;
    private final long lastUseSkillTime;
    private final long chargingFullTime;
    private final long lastUseUltimateSkillTime;
    private final UUID playerUUID;

    public SyncCardDataPacket(int cardId, long lastUseSkillTime, long chargingFullTime, long lastUseUltimateSkillTime, UUID playerUUID) {
        this.cardId = cardId;
        this.lastUseSkillTime = lastUseSkillTime;
        this.chargingFullTime = chargingFullTime;
        this.lastUseUltimateSkillTime = lastUseUltimateSkillTime;
        this.playerUUID = playerUUID;
    }

    public static void encode(SyncCardDataPacket packet, PacketBuffer buffer) {
        buffer.writeInt(packet.cardId);
        buffer.writeLong(packet.lastUseSkillTime);
        buffer.writeLong(packet.chargingFullTime);
        buffer.writeLong(packet.lastUseUltimateSkillTime);
        buffer.writeUniqueId(packet.playerUUID);
    }

    public static SyncCardDataPacket decode(PacketBuffer buffer){
        return new SyncCardDataPacket(buffer.readInt(), buffer.readLong(), buffer.readLong(), buffer.readLong(), buffer.readUniqueId());
    }

    public static void handlePacket(SyncCardDataPacket packet, Supplier<NetworkEvent.Context> content){
        NetworkEvent.Context context = content.get();
        context.enqueueWork(()->{
            if (context.getDirection().getReceptionSide().isClient()) {
                onClientCustomPack(packet, context);
            }
            if (context.getDirection().getReceptionSide().isServer()){
                onServerCustomPack(packet, context);
            }
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onClientCustomPack(SyncCardDataPacket packet, NetworkEvent.Context context){
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null){
            return;
        }

        Juggernaut.debug("收到卡牌数据包，玩家：" + packet.playerUUID + "，卡牌ID：" + packet.cardId);

        GameCard card = GameCardInit.getGameCardById(packet.cardId);
        if (card != null){
            PlayerGameData gameData = JuggernautClient.getInstance().getPlayerData(packet.playerUUID);
            gameData.setCardId(packet.cardId);
            gameData.setLastUseSkillTime(packet.lastUseSkillTime);
            gameData.setChargingFullTime(packet.chargingFullTime);
            gameData.setLastUseUltimateSkillTime(packet.lastUseUltimateSkillTime);
        }
    }

    public static void onServerCustomPack(SyncCardDataPacket packet, NetworkEvent.Context context){
        // 服务端不会收到这个包
    }
}
