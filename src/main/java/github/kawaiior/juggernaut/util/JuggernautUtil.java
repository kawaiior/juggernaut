package github.kawaiior.juggernaut.util;

import github.kawaiior.juggernaut.game.JuggernautServer;
import github.kawaiior.juggernaut.game.PlayerGameData;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;

public class JuggernautUtil {

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

}
