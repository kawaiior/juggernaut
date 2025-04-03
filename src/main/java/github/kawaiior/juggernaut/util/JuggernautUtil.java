package github.kawaiior.juggernaut.util;

import github.kawaiior.juggernaut.game.JuggernautServer;
import github.kawaiior.juggernaut.game.PlayerGameData;
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
            health.setBaseValue(30D);
        }
        player.setHealth(player.getMaxHealth());

        // 设置护甲
        PlayerGameData gameData = JuggernautServer.getInstance().getPlayerGameData(player);
        gameData.setMaxShield(20F);
        gameData.setShield(20F);
        gameData.syncCardData(player);
    }

    public static void removeJuggernautAttribute(ServerPlayerEntity player) {
        ModifiableAttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(20D);
        }

        // 移除护甲
        PlayerGameData gameData = JuggernautServer.getInstance().getPlayerGameData(player);
        gameData.setMaxShield(20F);
        gameData.setShield(20F);
        gameData.syncShieldData(player);
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

}
