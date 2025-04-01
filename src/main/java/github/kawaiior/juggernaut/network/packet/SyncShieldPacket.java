package github.kawaiior.juggernaut.network.packet;


import github.kawaiior.juggernaut.capability.ModCapability;
import github.kawaiior.juggernaut.capability.shield.ShieldPower;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

import static github.kawaiior.juggernaut.capability.shield.ShieldPower.SHIELD_DATA;

public class SyncShieldPacket {

    private final float shield;
    private final float maxShield;
    private final UUID playerUUID;

    public SyncShieldPacket(float shield, float maxShield, UUID playerUUID)    {
        this.shield = shield;
        this.maxShield = maxShield;
        this.playerUUID = playerUUID;
    }

    public float getShield() {
        return shield;
    }

    public float getMaxShield() {
        return maxShield;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public static void encode(SyncShieldPacket packet, PacketBuffer buffer) {
        buffer.writeFloat(packet.shield);
        buffer.writeFloat(packet.maxShield);
        buffer.writeUniqueId(packet.playerUUID);
    }

    public static SyncShieldPacket decode(PacketBuffer buffer){
        return new SyncShieldPacket(buffer.readFloat(), buffer.readFloat(), buffer.readUniqueId());
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
        SHIELD_DATA.put(packet.playerUUID,
                new ShieldPower.PlayerShieldData(packet.shield, packet.maxShield, packet.playerUUID));

        World world = Minecraft.getInstance().world;
        if (world == null){
            return;
        }
        for (PlayerEntity player: world.getPlayers()){
            if (player.getUniqueID().equals(packet.playerUUID)) {
                LazyOptional<ShieldPower> capability = player.getCapability(ModCapability.SHIELD_POWER);
                capability.ifPresent((power) -> {
                    power.setPlayerShield(packet.shield);
                    power.setPlayerMaxShield(packet.maxShield);
                });
                break;
            }
        }
    }

    public static void onServerCustomPack(SyncShieldPacket packet, NetworkEvent.Context context){
        // 服务端不会收到这个包
    }
}
