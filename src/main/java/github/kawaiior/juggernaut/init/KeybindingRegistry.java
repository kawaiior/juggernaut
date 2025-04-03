package github.kawaiior.juggernaut.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeybindingRegistry {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientRegistry.registerKeyBinding(KeyBoardInput.OPEN_SELECT_CARD_GUI);
            ClientRegistry.registerKeyBinding(KeyBoardInput.PLAYER_USE_SKILL);
            ClientRegistry.registerKeyBinding(KeyBoardInput.PLAYER_USE_ULTIMATE_SKILL);
        });
    }
}
