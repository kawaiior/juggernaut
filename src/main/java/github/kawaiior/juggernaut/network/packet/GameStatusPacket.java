package github.kawaiior.juggernaut.network.packet;


import github.kawaiior.juggernaut.render.hud.GameStatusRender;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GameStatusPacket {

    private final int status;
    private final long time;

    public GameStatusPacket(int status, long time) {
        this.status = status;
        this.time = time;
    }

    public static void encode(GameStatusPacket packet, PacketBuffer buffer) {
        buffer.writeInt(packet.status);
        buffer.writeLong(packet.time);
    }

    public static GameStatusPacket decode(PacketBuffer buffer){
        return new GameStatusPacket(buffer.readInt(), buffer.readLong());
    }

    public static void handlePacket(GameStatusPacket packet, Supplier<NetworkEvent.Context> content){
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
    public static void onClientCustomPack(GameStatusPacket packet, NetworkEvent.Context context){
        GameStatusRender.gameStatus = packet.status;
        GameStatusRender.gameStatusTime = packet.time;
    }

    public static void onServerCustomPack(GameStatusPacket packet, NetworkEvent.Context context){

    }
}
