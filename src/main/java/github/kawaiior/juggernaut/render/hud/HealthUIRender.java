package github.kawaiior.juggernaut.render.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.game.JuggernautClient;
import github.kawaiior.juggernaut.game.PlayerGameData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;


public class HealthUIRender extends AbstractGui {
    private final int width;
    private final int height;
    private final ResourceLocation HEALTH_UI = new ResourceLocation(Juggernaut.MOD_ID, "textures/hud/health.png");

    private final Minecraft minecraft;
    private MatrixStack matrixStack;

    public HealthUIRender(MatrixStack matrixStack) {
        this.minecraft = Minecraft.getInstance();
        this.width = this.minecraft.getMainWindow().getScaledWidth();
        this.height = this.minecraft.getMainWindow().getScaledHeight();
        this.matrixStack = matrixStack;
    }

    public void render() {
        PlayerEntity player = this.minecraft.player;
        if (player == null || player.isCreative() || player.isSpectator()){
            return;
        }

        // RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(HEALTH_UI);
        matrixStack.push();
        matrixStack.scale(0.35F, 0.35F, 1.0F);

        int baseHeight = (int) (height / 0.35F);

        float trueMaxHealth;
        float trueHealth = player.getHealth() + player.getAbsorptionAmount();

        if (trueHealth > player.getMaxHealth()){
            trueMaxHealth = trueHealth;
        }else {
            trueMaxHealth = player.getMaxHealth();
        }
        float healthPer = player.getHealth() / trueMaxHealth;
        if (healthPer < 0){
            healthPer = 0;
        }
        float absorptionPer = player.getAbsorptionAmount() / trueMaxHealth;
        if (absorptionPer < 0){
            absorptionPer = 0;
        }

        // 渲染血条
        this.blit(matrixStack, 20, baseHeight-43, 0, 0, 240, 24);

        this.blit(matrixStack, 20, baseHeight-43, 0, 24, (int)(240F * healthPer), 24);
        this.blit(matrixStack, 20 + (int)(240F * healthPer), baseHeight-43, 0, 144, (int)(240F * absorptionPer), 24);

        this.blit(matrixStack, 20, baseHeight-43, 0, 48, 240, 24);
        this.blit(matrixStack, 270, baseHeight-43, 0, 96, 24, 24);

        PlayerGameData gameData = JuggernautClient.getInstance().getPlayerData(player.getUniqueID());
        float trueMaxShield;
        float trueShield = gameData.getShield() + gameData.getTemporaryShield();
        if (trueShield > gameData.getMaxShield()){
            trueMaxShield = trueShield;
        }else {
            trueMaxShield = gameData.getMaxShield();
        }

        float shieldPer = gameData.getShield() / trueMaxShield;
        if (shieldPer < 0){
            shieldPer = 0;
        }
        float temporaryShieldPer = gameData.getTemporaryShield() / trueMaxShield;
        if (temporaryShieldPer < 0){
            temporaryShieldPer = 0;
        }

        // 渲染护甲
        this.blit(matrixStack, 20, baseHeight-73, 0, 0, 240, 24);

        this.blit(matrixStack, 20, baseHeight-73, 0, 72, (int)(240F * shieldPer), 24);
        this.blit(matrixStack, 20 + (int)(240F * shieldPer), baseHeight-73, 0, 120, (int)(240F * temporaryShieldPer), 24);
//        this.blit(matrixStack, 20 + (int)(240F * shieldPer) + (int)(240F * shieldOverflowPer), baseHeight-73, 0, 144, (int)(240F * absorptionPer), 24);

        this.blit(matrixStack, 20, baseHeight-73, 0, 48, 240, 24);
        this.blit(matrixStack, 270, baseHeight-73, 24, 96, 24, 24);

        matrixStack.pop();

        drawString(matrixStack, minecraft.fontRenderer, String.valueOf(MathHelper.ceil(trueHealth)), 105, this.height-15, 0xFFFFFF);
        drawString(matrixStack, minecraft.fontRenderer, String.valueOf((int)trueShield), 105, this.height-25, 0xCA7DF8);
    }
}
