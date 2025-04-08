package github.kawaiior.juggernaut.event;

import github.kawaiior.juggernaut.entity.render.PaintBubbleRender;
import github.kawaiior.juggernaut.entity.render.ReviveBeaconRender;
import github.kawaiior.juggernaut.entity.render.SuperPaintBubbleRender;
import github.kawaiior.juggernaut.entity.render.WatchBeaconRender;
import github.kawaiior.juggernaut.init.EntityTypeRegistry;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientSetUpEvent(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.REVIVE_BEACON_ENTITY.get(), ReviveBeaconRender::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.PAINT_BUBBLE_ENTITY.get(), PaintBubbleRender::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.SUPER_PAINT_BUBBLE_ENTITY.get(), SuperPaintBubbleRender::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.WATCH_BEACON_ENTITY.get(), WatchBeaconRender::new);

    }

}
