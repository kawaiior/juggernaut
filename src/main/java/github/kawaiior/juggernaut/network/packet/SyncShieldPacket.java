package github.kawaiior.juggernaut.network.packet;


import github.kawaiior.juggernaut.game.JuggernautClient;
import github.kawaiior.juggernaut.game.PlayerGameData;
import github.kawaiior.juggernaut.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncShieldPacket {

    private final float shield;
    private final float temporaryShield;
    private final float maxShield;
    private final UUID playerUUID;

    public SyncShieldPacket(float shield, float temporaryShield, float maxShield, UUID playerUUID) {
        this.shield = shield;
        this.temporaryShield = temporaryShield;
        this.maxShield = maxShield;
        this.playerUUID = playerUUID;
    }

    public static void encode(SyncShieldPacket packet, PacketBuffer buffer) {
        buffer.writeFloat(packet.shield);
        buffer.writeFloat(packet.temporaryShield);
        buffer.writeFloat(packet.maxShield);
        buffer.writeUniqueId(packet.playerUUID);
    }

    public static SyncShieldPacket decode(PacketBuffer buffer){
        return new SyncShieldPacket(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readUniqueId());
    }

    public static void handlePacket(SyncShieldPacket packet, Supplier<NetworkEvent.Context> content){
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
    public static void onClientCustomPack(SyncShieldPacket packet, NetworkEvent.Context context){
        World world = Minecraft.getInstance().world;
        if (world == null){
            return;
        }

        PlayerGameData gameData = JuggernautClient.getInstance().getPlayerData(packet.playerUUID);
        gameData.setShield(packet.shield);
        gameData.setTemporaryShield(packet.temporaryShield);
        gameData.setMaxShield(packet.maxShield);
    }

    public static void onServerCustomPack(SyncShieldPacket packet, NetworkEvent.Context context){
        // 服务端不会收到这个包
    }
}
