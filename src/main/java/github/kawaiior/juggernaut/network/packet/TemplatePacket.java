package github.kawaiior.juggernaut.network.packet;


import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TemplatePacket {

    public static void encode(TemplatePacket packet, PacketBuffer buffer) {

    }

    public static TemplatePacket decode(PacketBuffer buffer){
        return new TemplatePacket();
    }

    public static void handlePacket(TemplatePacket packet, Supplier<NetworkEvent.Context> content){
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
    public static void onClientCustomPack(TemplatePacket packet, NetworkEvent.Context context){

    }

    public static void onServerCustomPack(TemplatePacket packet, NetworkEvent.Context context){

    }
}
