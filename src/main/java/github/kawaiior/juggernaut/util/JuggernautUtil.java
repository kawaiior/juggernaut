package github.kawaiior.juggernaut.util;

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
    }

    public static void removeJuggernautAttribute(ServerPlayerEntity player) {
        ModifiableAttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(20D);
        }
    }

}
