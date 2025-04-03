package github.kawaiior.juggernaut.game;

import github.kawaiior.juggernaut.util.EntityUtil;
import net.minecraft.client.entity.player.ClientPlayerEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JuggernautClient {

    private static final JuggernautClient INSTANCE = new JuggernautClient();
    private static final Map<UUID, PlayerGameData> CLIENT_PLAYER_MAP = new ConcurrentHashMap<>();

    public static JuggernautClient getInstance() {
        return INSTANCE;
    }

    public PlayerGameData getPlayerData(UUID uuid) {
        // auto create
        PlayerGameData gameData = CLIENT_PLAYER_MAP.get(uuid);
        if (gameData == null){
            gameData = new PlayerGameData(EntityUtil.getPlayerNameByUUID(uuid));
            JuggernautClient.getInstance().getPlayerDataMap().put(uuid, gameData);
        }
        return gameData;
    }

    public Map<UUID, PlayerGameData> getPlayerDataMap() {
        return CLIENT_PLAYER_MAP;
    }

}
