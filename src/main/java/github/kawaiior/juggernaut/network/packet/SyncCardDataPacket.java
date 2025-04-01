package github.kawaiior.juggernaut.network.packet;


import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.capability.ModCapability;
import github.kawaiior.juggernaut.capability.card.CardPower;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCardDataPacket {

    private final int cardId;
    private final long lastUseSkillTime;
    private final long lastUseUltimateSkillTime;
    private final float ultimateSkillEnergy;

    public SyncCardDataPacket(int cardId, long lastUseSkillTime, long lastUseUltimateSkillTime, float ultimateSkillEnergy) {
        this.cardId = cardId;
        this.lastUseSkillTime = lastUseSkillTime;
        this.lastUseUltimateSkillTime = lastUseUltimateSkillTime;
        this.ultimateSkillEnergy = ultimateSkillEnergy;
    }

    public static void encode(SyncCardDataPacket packet, PacketBuffer buffer) {
        buffer.writeInt(packet.cardId);
        buffer.writeLong(packet.lastUseSkillTime);
        buffer.writeLong(packet.lastUseUltimateSkillTime);
        buffer.writeFloat(packet.ultimateSkillEnergy);
    }

    public static SyncCardDataPacket decode(PacketBuffer buffer){
        return new SyncCardDataPacket(buffer.readInt(), buffer.readLong(), buffer.readLong(), buffer.readFloat());
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
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null){
            return;
        }

        LazyOptional<CardPower> capability = player.getCapability(ModCapability.CARD_POWER);
        capability.ifPresent((power) -> {
            power.setCardId(packet.cardId);
            power.setLastUseSkillTime(packet.lastUseSkillTime);
            power.setLastUseUltimateSkillTime(packet.lastUseUltimateSkillTime);
            power.setUltimateSkillEnergy(packet.ultimateSkillEnergy);

            Juggernaut.debug("收到卡牌数据包: " + packet.cardId + ", " + packet.lastUseSkillTime + ", " + packet.lastUseUltimateSkillTime + ", " + packet.ultimateSkillEnergy);
        });
    }

    public static void onServerCustomPack(SyncCardDataPacket packet, NetworkEvent.Context context){
        // 服务端不会收到这个包
    }
}
