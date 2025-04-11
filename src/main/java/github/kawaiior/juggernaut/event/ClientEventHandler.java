package github.kawaiior.juggernaut.event;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.entity.render.*;
import github.kawaiior.juggernaut.init.EntityTypeRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
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
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.SUPER_WATCH_BEACON_ENTITY.get(), SuperWatchBeaconRender::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.ELECTRIC_BALL_ENTITY.get(), ElectricBallRender::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.BABYLON_WEAPON_ENTITY.get(), RenderBabylonWeapon::new);
    }

    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(Juggernaut.MOD_ID, path);
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent evt) {
        for (int i = 0; i < RenderBabylonWeapon.WEAPON_TYPES; i++) {
            ModelLoader.addSpecialModel(prefix("icon/gate_weapon_" + i));
        }
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent evt) {
        for (int i = 0; i < RenderBabylonWeapon.WEAPON_TYPES; i++) {
            RenderBabylonWeapon.kingKeyWeaponModels[i] = evt.getModelRegistry().get(prefix("icon/gate_weapon_" + i));
        }
    }

}
