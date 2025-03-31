package github.kawaiior.juggernaut.network.packet;

import github.kawaiior.juggernaut.capability.ShieldPower.PlayerShieldData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static github.kawaiior.juggernaut.capability.ShieldPower.SHIELD_DATA;

public class SyncAllPlayerShieldPacket {

    private final List<PlayerShieldData> playerShieldData;

    public SyncAllPlayerShieldPacket(List<PlayerShieldData> playerShieldData) {
        this.playerShieldData = playerShieldData;
    }

    public List<PlayerShieldData> getPlayerShieldData() {
        return playerShieldData;
    }

    public static void encode(SyncAllPlayerShieldPacket packet, PacketBuffer buffer) {
        buffer.writeInt(packet.getPlayerShieldData().size());
        for (PlayerShieldData data : packet.getPlayerShieldData()) {
            buffer.writeFloat(data.shield);
            buffer.writeFloat(data.maxShield);
            buffer.writeUniqueId(data.playerUUID);
        }
    }

    public static SyncAllPlayerShieldPacket decode(PacketBuffer buffer){
        List<PlayerShieldData> shieldList = new ArrayList<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            shieldList.add(new PlayerShieldData(buffer.readFloat(), buffer.readFloat(), buffer.readUniqueId()));
        }
        return new SyncAllPlayerShieldPacket(shieldList);
    }

    public static void handlePacket(SyncAllPlayerShieldPacket packet, Supplier<NetworkEvent.Context> content){
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
    public static void onClientCustomPack(SyncAllPlayerShieldPacket packet, NetworkEvent.Context context){
        for (PlayerShieldData data : packet.getPlayerShieldData()) {
            SHIELD_DATA.put(data.playerUUID, data);
        }
    }

    public static void onServerCustomPack(SyncAllPlayerShieldPacket packet, NetworkEvent.Context context){
        // 服务端不会收到这个包
    }
}
