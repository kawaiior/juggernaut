package github.kawaiior.juggernaut.util;

import github.kawaiior.juggernaut.card.GameCardInit;
import github.kawaiior.juggernaut.game.Constants;
import github.kawaiior.juggernaut.game.GameData;
import github.kawaiior.juggernaut.game.GameServer;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JuggernautUtil {

    private static final Map<UUID, ServerPlayerEntity> UUID_SERVER_PLAYER_ENTITY_MAP = new ConcurrentHashMap<>();

    public static void setJuggernautAttribute(ServerPlayerEntity player) {
        ModifiableAttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(Constants.JUGGERNAUT_MAX_HEALTH);
        }
        player.setHealth(player.getMaxHealth());

        // 设置护甲
        GameData gameData = GameServer.getInstance().getPlayerGameData(player);
        if (gameData != null ){
            // 设置card
            gameData.getCardData().lastCardId = gameData.getCardData().cardId;
            gameData.getCardData().cardId = GameCardInit.GILGAMESH.getCardId();
            gameData.getShieldData().maxShield = Constants.JUGGERNAUT_MAX_SHIELD;
            gameData.getShieldData().shield = gameData.getShieldData().maxShield;
            gameData.getCardData().syncData(player);
            gameData.getShieldData().syncData(player);
        }
    }

    public static void removeJuggernautAttribute(ServerPlayerEntity player) {
        ModifiableAttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(Constants.PLAYER_MAX_HEALTH);
        }

        // 移除护甲
        GameData gameData = GameServer.getInstance().getPlayerGameData(player);
        if (gameData != null){
            // 重置card
            gameData.getCardData().cardId = gameData.getCardData().lastCardId;
            gameData.getShieldData().maxShield = Constants.PLAYER_MAX_SHIELD;
            gameData.getShieldData().shield = gameData.getShieldData().maxShield;
            gameData.getCardData().syncData(player);
            gameData.getShieldData().syncData(player);
        }
    }

    public static void setUUIDServerPlayerEntityMap(ServerPlayerEntity player) {
        UUID_SERVER_PLAYER_ENTITY_MAP.put(player.getUniqueID(), player);
    }

    @Nullable
    public static ServerPlayerEntity getServerPlayerEntity(UUID uuid) {
        if (uuid == null){
            return null;
        }
        return UUID_SERVER_PLAYER_ENTITY_MAP.get(uuid);
    }

    public static void teleportPlayerToPlayground(ServerPlayerEntity player) {
        player.moveForced(
                Constants.SPAWN_POS.getX() + Constants.RANDOM.nextDouble() * (Constants.SPAWN_HOME_X_WIDTH / 2D),
                Constants.SPAWN_POS.getY(),
                Constants.SPAWN_POS.getZ() + Constants.RANDOM.nextDouble() * (Constants.SPAWN_HOME_Z_WIDTH / 2D)
        );
    }

    public static void teleportJuggernautToPlayground(ServerPlayerEntity player) {
        // TODO
        player.moveForced(Constants.JUGGERNAUT_POS.getX(), Constants.JUGGERNAUT_POS.getY(), Constants.JUGGERNAUT_POS.getZ());
    }

    public static void teleportPlayerToReadyHome(ServerPlayerEntity player) {
        player.moveForced(
                Constants.READY_HOME_POS.getX() + Constants.RANDOM.nextDouble() * 5,
                Constants.READY_HOME_POS.getY(),
                Constants.READY_HOME_POS.getZ() + Constants.RANDOM.nextDouble() * 5
        );
    }
}
