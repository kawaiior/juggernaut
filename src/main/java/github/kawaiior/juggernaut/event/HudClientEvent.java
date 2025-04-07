package github.kawaiior.juggernaut.event;

import github.kawaiior.juggernaut.render.hud.*;
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
                event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE ||
                event.getType() == RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
            event.setCanceled(true);
            return;
        }

        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || Minecraft.getInstance().player == null) {
            return;
        }
        // 渲染击杀板
        DeathBoardGui deathBoard = new DeathBoardGui(event.getMatrixStack());
        deathBoard.render();
        // 渲染生命值与护甲值
        HealthUIRender healthUIRender = new HealthUIRender(event.getMatrixStack());
        healthUIRender.render();
        // 渲染游戏状态
        GameStatusRender gameStatusRender = new GameStatusRender(event.getMatrixStack());
        gameStatusRender.render();
        // 渲染技能充能
        SkillChargingUIRender skillChargingUIRender = new SkillChargingUIRender(event.getMatrixStack());
        skillChargingUIRender.render();
        // 渲染GAME DATA
        if (GameDataRender.render){
            GameDataRender gameDataRender = new GameDataRender(event.getMatrixStack());
            gameDataRender.render();
        }
    }

}
