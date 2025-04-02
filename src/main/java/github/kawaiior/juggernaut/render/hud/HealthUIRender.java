package github.kawaiior.juggernaut.render.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.capability.shield.ShieldPower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import static github.kawaiior.juggernaut.capability.shield.ShieldPower.SHIELD_DATA;

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

        // 渲染血条
        this.blit(matrixStack, 20, baseHeight-43, 0, 0, 240, 24);
        this.blit(matrixStack, 20, baseHeight-43, 0, 24, (int)(240F *(player.getHealth() / player.getMaxHealth())), 24);
        this.blit(matrixStack, 20, baseHeight-43, 0, 48, 240, 24);
        this.blit(matrixStack, 270, baseHeight-43, 0, 96, 24, 24);

        int shield = 0;
        ShieldPower.PlayerShieldData shieldData = SHIELD_DATA.get(player.getUniqueID());
        if (shieldData != null){
            float shieldOverflow = shieldData.shield - shieldData.maxShield;
            if (shieldOverflow < 0){
                shieldOverflow = 0;
            }

            float absorptionAmount = player.getAbsorptionAmount();

            float trueMaxShield;
            float trueShield;
            if (shieldData.shield < shieldData.maxShield){
                trueMaxShield = shieldData.maxShield + absorptionAmount;
                trueShield = shieldData.shield;
            }else {
                trueMaxShield = shieldData.shield + absorptionAmount;
                trueShield = shieldData.maxShield;
            }

            float shieldPer = trueShield / trueMaxShield;
            if (shieldPer < 0){
                shieldPer = 0;
            }
            float shieldOverflowPer = shieldOverflow / trueMaxShield;
            if (shieldOverflowPer < 0){
                shieldOverflowPer = 0;
            }
            float absorptionPer = absorptionAmount / trueMaxShield;
            if (absorptionPer < 0){
                absorptionPer = 0;
            }

            shield = (int) shieldData.shield + (int) absorptionAmount;

            // 渲染护甲
            this.blit(matrixStack, 20, baseHeight-73, 0, 0, 240, 24);

            this.blit(matrixStack, 20, baseHeight-73, 0, 72, (int)(240F * shieldPer), 24);
            this.blit(matrixStack, 20 + (int)(240F * shieldPer), baseHeight-73, 0, 120, (int)(240F * shieldOverflowPer), 24);
            this.blit(matrixStack, 20 + (int)(240F * shieldPer) + (int)(240F * shieldOverflowPer), baseHeight-73, 0, 144, (int)(240F * absorptionPer), 24);

            this.blit(matrixStack, 20, baseHeight-73, 0, 48, 240, 24);
            this.blit(matrixStack, 270, baseHeight-73, 24, 96, 24, 24);
        }

        matrixStack.pop();

        drawString(matrixStack, minecraft.fontRenderer, String.valueOf(MathHelper.ceil(player.getHealth())), 105, this.height-15, 0xFFFFFF);
        drawString(matrixStack, minecraft.fontRenderer, String.valueOf(shield), 105, this.height-25, 0xCA7DF8);
    }
}
