package github.kawaiior.juggernaut.network.packet;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PlayerUseSkillRequestPacket {

    private final boolean ultimate;
    private final boolean hasTarget;
    private final UUID targetUUID;

    public PlayerUseSkillRequestPacket(boolean ultimate, boolean hasTarget, UUID targetUUID) {
        this.ultimate = ultimate;
        this.hasTarget = hasTarget;
        this.targetUUID = targetUUID;
    }

    public static void encode(PlayerUseSkillRequestPacket packet, PacketBuffer buffer) {
        buffer.writeBoolean(packet.ultimate);
        buffer.writeBoolean(packet.hasTarget);
        if (packet.hasTarget) {
            buffer.writeUniqueId(packet.targetUUID);
        }
    }

    public static PlayerUseSkillRequestPacket decode(PacketBuffer buffer){
        boolean ultimate = buffer.readBoolean();
        boolean hasTarget = buffer.readBoolean();
        UUID targetUUID = hasTarget ? buffer.readUniqueId() : null;
        return new PlayerUseSkillRequestPacket(ultimate, hasTarget, targetUUID);
    }

    public static void handlePacket(PlayerUseSkillRequestPacket packet, Supplier<NetworkEvent.Context> content){
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
    public static void onClientCustomPack(PlayerUseSkillRequestPacket packet, NetworkEvent.Context context){
        // 客户端不会收到这个包
    }

    public static void onServerCustomPack(PlayerUseSkillRequestPacket packet, NetworkEvent.Context context){
        ServerPlayerEntity player = context.getSender();
    }
}
