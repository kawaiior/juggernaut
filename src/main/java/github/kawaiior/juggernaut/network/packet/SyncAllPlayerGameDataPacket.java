package github.kawaiior.juggernaut.network.packet;


import github.kawaiior.juggernaut.game.JuggernautClient;
import github.kawaiior.juggernaut.game.PlayerGameData;
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

    public static class GameData{
        public UUID playerUUID;
        public String playerName;
        public int jKillCount;
        public int killCount;
        public int deathCount;
        public float damageAmount;
        public float bearDamage;
        public boolean juggernaut;
    }

    private final List<GameData> gameDataList;

    public SyncAllPlayerGameDataPacket(Map<ServerPlayerEntity, PlayerGameData> map) {
        this.gameDataList = new ArrayList<>();
        map.forEach((player, playerGameData) -> {
            GameData gameData = new GameData();
            gameData.playerUUID = player.getUniqueID();
            gameData.playerName = player.getScoreboardName();
            gameData.jKillCount = playerGameData.getjKillCount();
            gameData.killCount = playerGameData.getKillCount();
            gameData.deathCount = playerGameData.getDeathCount();
            gameData.damageAmount = playerGameData.getDamageAmount();
            gameData.bearDamage = playerGameData.getBearDamage();
            gameData.juggernaut = playerGameData.isJuggernaut();
            gameDataList.add(gameData);
        });
    }

    public SyncAllPlayerGameDataPacket(List<GameData> gameDataList)  {
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
        List<GameData> gameDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            GameData gameData = new GameData();
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
        List<GameData> gameDataList = packet.gameDataList;
        JuggernautClient client = JuggernautClient.getInstance();
        for (GameData gameData : gameDataList) {
            PlayerGameData playerGameData = client.getPlayerData(gameData.playerUUID);
            playerGameData.setjKillCount(gameData.jKillCount);
            playerGameData.setKillCount(gameData.killCount);
            playerGameData.setDeathCount(gameData.deathCount);
            playerGameData.setDamageAmount(gameData.damageAmount);
            playerGameData.setBearDamage(gameData.bearDamage);
            playerGameData.setJuggernaut(gameData.juggernaut);
        }
    }

    public static void onServerCustomPack(SyncAllPlayerGameDataPacket packet, NetworkEvent.Context context){
        // 服务端不会收到这个包
    }
}
