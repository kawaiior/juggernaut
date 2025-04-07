package github.kawaiior.juggernaut.render.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.game.JuggernautClient;
import github.kawaiior.juggernaut.game.PlayerGameData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameDataRender extends AbstractGui {

    public static class RenderData{
        public String name;
        public int jkill;
        public int kill;
        public int death;
        public int damage;
        public int damageBear;
        public int ping;
        public boolean self;
        public boolean juggernaut;
    }

    public static boolean render = false;
    private final ResourceLocation GAME_DATA_UI = new ResourceLocation(Juggernaut.MOD_ID, "textures/hud/game_data.png");

    private final Minecraft minecraft;
    private MatrixStack matrixStack;
    private final int width;
    private final int height;

    public GameDataRender(MatrixStack matrixStack) {
        this.minecraft = Minecraft.getInstance();
        this.matrixStack = matrixStack;
        this.width = this.minecraft.getMainWindow().getScaledWidth();
        this.height = this.minecraft.getMainWindow().getScaledHeight();
    }

    public void render() {

        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null){
            return;
        }

        this.minecraft.getTextureManager().bindTexture(GAME_DATA_UI);

        int baseX = (width-320)/2;
        int kdaX = baseX + 65 + 64;
        int damageX = baseX + 138 + 64;
        int damageBearX = baseX + 182 + 64;
        int pingX = baseX + 226 + 64;

        GlStateManager.pushMatrix();
        // 透明度
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepthTest();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

        for (int i = 0; i < 13; i++) {
            this.blit(matrixStack, baseX, 20 + 12 * i, 0, 0, 256, 12);
            this.blit(matrixStack, baseX+256, 20 + 12 * i, 0, 0, 64, 12);
        }

        // render border
        for (int i = 0; i < 13; i++) {
            this.blit(matrixStack, baseX, 31 + 12 * i, 0, 39, 256, 1);
            this.blit(matrixStack, baseX+256, 31 + 12 * i, 0, 39, 64, 1);
        }

        GlStateManager.popMatrix();

        // handle map
        Map<UUID, PlayerGameData> map = JuggernautClient.getInstance().getPlayerDataMap();
        List<RenderData> renderDataList = new ArrayList<>();
        map.forEach((uuid, data) -> {
            RenderData renderData = new RenderData();
            renderData.name = data.getPlayerName();
            renderData.jkill = data.getJKillCount();
            renderData.kill = data.getKillCount();
            renderData.death = data.getDeathCount();
            renderData.damage = (int) data.getDamageAmount();
            renderData.damageBear = (int) data.getBearDamage();
            renderData.self = uuid.equals(player.getUniqueID());
            renderData.juggernaut = data.isJuggernaut();
            NetworkPlayerInfo info = player.connection.getPlayerInfo(uuid);
            if (info != null){
                renderData.ping = info.getResponseTime();
            }else {
                renderData.ping = 9999;
            }
            renderDataList.add(renderData);
        });

        // render special
        for (int i = 0; i < renderDataList.size(); i++) {
            RenderData renderData = renderDataList.get(i);
            if (renderData.self){
                this.blit(matrixStack, baseX, 32 + 12 * i, 0, 13, 256, 12);
                this.blit(matrixStack, baseX+256, 32 + 12 * i, 0, 13, 64, 12);
            }else if (renderData.juggernaut){
                this.blit(matrixStack, baseX, 32 + 12 * i, 0, 26, 256, 12);
                this.blit(matrixStack, baseX+256, 32 + 12 * i, 0, 26, 64, 12);
            }
        }

        // render title
        drawString(matrixStack, minecraft.fontRenderer, "K / K / D", kdaX, 22, 0xFFFFFF);
        drawString(matrixStack, minecraft.fontRenderer, "输出伤害", damageX, 22, 0xFFFFFF);
        drawString(matrixStack, minecraft.fontRenderer, "承受伤害", damageBearX, 22, 0xFFFFFF);
        drawString(matrixStack, minecraft.fontRenderer, "PING", pingX, 22, 0xFFFFFF);

        int fontWidth1 = minecraft.fontRenderer.getStringWidth("K / K / D");
        int fontWidth2 = minecraft.fontRenderer.getStringWidth("输出伤害");
        int fontWidth3 = minecraft.fontRenderer.getStringWidth("承受伤害");
        int fontWidth4 = minecraft.fontRenderer.getStringWidth("PING");

        int posCenter1 = kdaX + fontWidth1 / 2;
        int posCenter2 = damageX + fontWidth2 / 2;
        int posCenter3 = damageBearX + fontWidth3 / 2;
        int posCenter4 = pingX + fontWidth4 / 2;

        // render data
        for (int i = 0; i < renderDataList.size(); i++) {
            RenderData renderData = renderDataList.get(i);
            String playerName = renderData.name;
            String font1 = String.format("%d / %d / %d", renderData.jkill, renderData.kill, renderData.death);
            String font2 = String.format("%d", renderData.damage);
            String font3 = String.format("%d", renderData.damageBear);
            String font4 = String.format("%d", renderData.ping);

            int height = 34 + i * 12;

            // render
            drawString(matrixStack, minecraft.fontRenderer, playerName, baseX + 5, height, 0xFFFFFF);
            drawString(matrixStack, minecraft.fontRenderer, font1, posCenter1 - minecraft.fontRenderer.getStringWidth(font1) / 2, height, 0xFFFFFF);
            drawString(matrixStack, minecraft.fontRenderer, font2, posCenter2 - minecraft.fontRenderer.getStringWidth(font2) / 2, height, 0xFFFFFF);
            drawString(matrixStack, minecraft.fontRenderer, font3, posCenter3 - minecraft.fontRenderer.getStringWidth(font3) / 2, height, 0xFFFFFF);
            drawString(matrixStack, minecraft.fontRenderer, font4, posCenter4 - minecraft.fontRenderer.getStringWidth(font4) / 2, height, 0xFFFFFF);
        }
    }
}
