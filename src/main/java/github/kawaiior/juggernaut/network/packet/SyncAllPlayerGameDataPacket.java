package github.kawaiior.juggernaut.network.packet;


import github.kawaiior.juggernaut.game.GameData;
import github.kawaiior.juggernaut.game.JuggernautClient;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncAllPlayerGameDataPacket {

    public static class ThisGameData{
        public UUID playerUUID;
        public String playerName;
        public int jKillCount;
        public int killCount;
        public int deathCount;
        public float damageAmount;
        public float bearDamage;
        public boolean juggernaut;
    }

    private final List<ThisGameData> gameDataList;

    public SyncAllPlayerGameDataPacket(Map<ServerPlayerEntity, GameData> map) {
        this.gameDataList = new ArrayList<>();
        map.forEach((player, playerGameData) -> {
            ThisGameData gameData = new ThisGameData();
            gameData.playerUUID = player.getUniqueID();
            gameData.playerName = player.getScoreboardName();
            gameData.jKillCount = playerGameData.getBoardData().jKillCount;
            gameData.killCount = playerGameData.getBoardData().killCount;
            gameData.deathCount = playerGameData.getBoardData().deathCount;
            gameData.damageAmount = playerGameData.getBoardData().damageAmount;
            gameData.bearDamage = playerGameData.getBoardData().bearDamage;
            gameData.juggernaut = playerGameData.getBoardData().juggernaut;
            gameDataList.add(gameData);
        });
    }

    public SyncAllPlayerGameDataPacket(List<ThisGameData> gameDataList)  {
        this.gameDataList = gameDataList;
    }

    public static void encode(SyncAllPlayerGameDataPacket packet, PacketBuffer buffer) {
        buffer.writeVarInt(packet.gameDataList.size());
        packet.gameDataList.forEach(gameData -> {
            buffer.writeUniqueId(gameData.playerUUID);
            buffer.writeString(gameData.playerName);
            buffer.writeInt(gameData.jKillCount);
            buffer.writeInt(gameData.killCount);
            buffer.writeInt(gameData.deathCount);
            buffer.writeFloat(gameData.damageAmount);
            buffer.writeFloat(gameData.bearDamage);
            buffer.writeBoolean(gameData.juggernaut);
        });
    }

    public static SyncAllPlayerGameDataPacket decode(PacketBuffer buffer){
        int size = buffer.readVarInt();
        List<ThisGameData> gameDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ThisGameData gameData = new ThisGameData();
            gameData.playerUUID = buffer.readUniqueId();
            gameData.playerName = buffer.readString();
            gameData.jKillCount = buffer.readInt();
            gameData.killCount = buffer.readInt();
            gameData.deathCount = buffer.readInt();
            gameData.damageAmount = buffer.readFloat();
            gameData.bearDamage = buffer.readFloat();
            gameData.juggernaut = buffer.readBoolean();
            gameDataList.add(gameData);
        }
        return new SyncAllPlayerGameDataPacket(gameDataList);
    }

    public static void handlePacket(SyncAllPlayerGameDataPacket packet, Supplier<NetworkEvent.Context> content){
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
    public static void onClientCustomPack(SyncAllPlayerGameDataPacket packet, NetworkEvent.Context context){
        List<ThisGameData> gameDataList = packet.gameDataList;
        JuggernautClient client = JuggernautClient.getInstance();
        for (ThisGameData gameData : gameDataList) {
            GameData playerGameData = client.getPlayerData(gameData.playerUUID);
            playerGameData.getBoardData().jKillCount = gameData.jKillCount;
            playerGameData.getBoardData().killCount = gameData.killCount;
            playerGameData.getBoardData().deathCount = gameData.deathCount;
            playerGameData.getBoardData().damageAmount = gameData.damageAmount;
            playerGameData.getBoardData().bearDamage = gameData.bearDamage;
            playerGameData.getBoardData().juggernaut = gameData.juggernaut;
        }
    }

    public static void onServerCustomPack(SyncAllPlayerGameDataPacket packet, NetworkEvent.Context context){
        // 服务端不会收到这个包
    }
}
