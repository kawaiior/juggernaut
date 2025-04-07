package github.kawaiior.juggernaut.network.packet;


import github.kawaiior.juggernaut.game.JuggernautClient;
import github.kawaiior.juggernaut.game.PlayerGameData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncPlayerGameDataPacket {
    private final UUID playerUUID;
    private final String playerName;
    private final int jKillCount;
    private final int killCount;
    private final int deathCount;
    private final float damageAmount;
    private final float bearDamage;
    private final boolean juggernaut;

    public SyncPlayerGameDataPacket(UUID playerUUID, PlayerGameData gameData) {
        this.playerUUID = playerUUID;
        this.playerName = gameData.getPlayerName();
        this.jKillCount = gameData.getjKillCount();
        this.killCount = gameData.getKillCount();
        this.deathCount = gameData.getDeathCount();
        this.damageAmount = gameData.getDamageAmount();
        this.bearDamage = gameData.getBearDamage();
        this.juggernaut = gameData.isJuggernaut();
    }

    public SyncPlayerGameDataPacket(UUID playerUUID, String playerName, int jKillCount, int killCount, int deathCount, float damageAmount, float bearDamage, boolean juggernaut) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.jKillCount = jKillCount;
        this.killCount = killCount;
        this.deathCount = deathCount;
        this.damageAmount = damageAmount;
        this.bearDamage = bearDamage;
        this.juggernaut = juggernaut;
    }

    public static void encode(SyncPlayerGameDataPacket packet, PacketBuffer buffer) {
        buffer.writeUniqueId(packet.playerUUID);
        buffer.writeString(packet.playerName);
        buffer.writeInt(packet.jKillCount);
        buffer.writeInt(packet.killCount);
        buffer.writeInt(packet.deathCount);
        buffer.writeFloat(packet.damageAmount);
        buffer.writeFloat(packet.bearDamage);
        buffer.writeBoolean(packet.juggernaut);
    }

    public static SyncPlayerGameDataPacket decode(PacketBuffer buffer){
        return new SyncPlayerGameDataPacket(buffer.readUniqueId(), buffer.readString(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readFloat(), buffer.readFloat(), buffer.readBoolean());
    }

    public static void handlePacket(SyncPlayerGameDataPacket packet, Supplier<NetworkEvent.Context> content){
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
    public static void onClientCustomPack(SyncPlayerGameDataPacket packet, NetworkEvent.Context context){
        PlayerGameData gameData = JuggernautClient.getInstance().getPlayerData(packet.playerUUID);
        gameData.setjKillCount(packet.jKillCount);
        gameData.setKillCount(packet.killCount);
        gameData.setDeathCount(packet.deathCount);
        gameData.setDamageAmount(packet.damageAmount);
        gameData.setBearDamage(packet.bearDamage);
        gameData.setJuggernaut(packet.juggernaut);
    }

    public static void onServerCustomPack(SyncPlayerGameDataPacket packet, NetworkEvent.Context context){
        // 服务端不会收到这个包
    }
}
