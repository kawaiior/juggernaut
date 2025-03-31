package github.kawaiior.juggernaut.network.packet;

import github.kawaiior.juggernaut.render.hud.DeathBoardGui;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DeathBoardMsgPacket {

    private String message = "";

    public DeathBoardMsgPacket(String msg){
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }

    public static void encode(DeathBoardMsgPacket packet, PacketBuffer buffer) {
        buffer.writeString(packet.getMessage());
    }

    public static DeathBoardMsgPacket decode(PacketBuffer buffer){
        return new DeathBoardMsgPacket(buffer.readString());
    }

    public static void handlePacket(DeathBoardMsgPacket packet, Supplier<NetworkEvent.Context> content){
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
    public static void onClientCustomPack(DeathBoardMsgPacket packet, NetworkEvent.Context context){
        DeathBoardGui.receiveMsg(packet.getMessage());
    }

    public static void onServerCustomPack(DeathBoardMsgPacket packet, NetworkEvent.Context context){
        // 服务端不会收到这个包
    }

}
