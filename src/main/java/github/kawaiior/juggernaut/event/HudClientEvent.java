package github.kawaiior.juggernaut.event;

import github.kawaiior.juggernaut.render.hud.DeathBoardGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class HudClientEvent {

    @SubscribeEvent
    public static void onOverlayRender(RenderGameOverlayEvent event){
        // 不再渲染饱食度 生命值 经验值
        if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD ||
                event.getType() == RenderGameOverlayEvent.ElementType.HEALTH ||
                event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE){
            event.setCanceled(true);
            return;
        }

        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || Minecraft.getInstance().player == null) {
            return;
        }
        // 渲染击杀板
        DeathBoardGui deathBoard = new DeathBoardGui(event.getMatrixStack());
        deathBoard.render();
    }

}
