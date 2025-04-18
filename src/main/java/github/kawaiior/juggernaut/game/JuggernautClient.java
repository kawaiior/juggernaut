package github.kawaiior.juggernaut.game;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JuggernautClient {

    private static final JuggernautClient INSTANCE = new JuggernautClient();
    private static final Map<UUID, GameData> CLIENT_PLAYER_MAP = new ConcurrentHashMap<>();

    public static JuggernautClient getInstance() {
        return INSTANCE;
    }

    @Nonnull
    public GameData getPlayerData(@Nonnull UUID uuid) {
        // auto create
        GameData gameData = CLIENT_PLAYER_MAP.get(uuid);
        if (gameData == null){
            gameData = new GameData();
            JuggernautClient.getInstance().getPlayerDataMap().put(uuid, gameData);
        }
        return gameData;
    }

    public Map<UUID, GameData> getPlayerDataMap() {
        return CLIENT_PLAYER_MAP;
    }

}
