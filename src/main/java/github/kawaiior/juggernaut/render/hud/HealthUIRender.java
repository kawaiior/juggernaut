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

        float shieldPer = 0;
        int shield = 0;
        ShieldPower.PlayerShieldData shieldData = SHIELD_DATA.get(player.getUniqueID());
        if (shieldData != null){
            shield = (int)(shieldData.shield);
            shieldPer = shieldData.shield / shieldData.maxShield;
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(HEALTH_UI);
        matrixStack.push();
        matrixStack.scale(0.35F, 0.35F, 1.0F);

        int baseHeight = (int) (height / 0.35F);

        // 渲染护甲
        this.blit(matrixStack, 20, baseHeight-73, 0, 0, 240, 24);
        this.blit(matrixStack, 20, baseHeight-73, 0, 72, (int)(240F * shieldPer), 24);
        this.blit(matrixStack, 20, baseHeight-73, 0, 48, 240, 24);
        this.blit(matrixStack, 270, baseHeight-73, 24, 96, 24, 24);

        // 渲染血条
        this.blit(matrixStack, 20, baseHeight-43, 0, 0, 240, 24);
        this.blit(matrixStack, 20, baseHeight-43, 0, 24, (int)(240F *(player.getHealth() / player.getMaxHealth())), 24);
        this.blit(matrixStack, 20, baseHeight-43, 0, 48, 240, 24);
        this.blit(matrixStack, 270, baseHeight-43, 0, 96, 24, 24);

        matrixStack.pop();

        drawString(matrixStack, minecraft.fontRenderer, String.valueOf(MathHelper.ceil(player.getHealth())), 105, this.height-15, 0xFFFFFF);
        drawString(matrixStack, minecraft.fontRenderer, String.valueOf(shield), 105, this.height-25, 0xCA7DF8);
    }
}
