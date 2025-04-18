package github.kawaiior.juggernaut.render.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.card.GameCard;
import github.kawaiior.juggernaut.game.GameData;
import github.kawaiior.juggernaut.game.JuggernautClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public class SkillChargingUIRender extends AbstractGui {
    private final Minecraft minecraft;
    private MatrixStack matrixStack;
    private final int width;
    private final int height;
    private final ResourceLocation SKILL_UI = new ResourceLocation(Juggernaut.MOD_ID, "textures/hud/skill.png");

    public SkillChargingUIRender(MatrixStack matrixStack) {
        this.minecraft = Minecraft.getInstance();
        this.matrixStack = matrixStack;
        this.width = this.minecraft.getMainWindow().getScaledWidth();
        this.height = this.minecraft.getMainWindow().getScaledHeight();
    }

    public void render() {
        long now = System.currentTimeMillis();
        PlayerEntity player = this.minecraft.player;
        if (player == null || player.isCreative() || player.isSpectator()){
            return;
        }
        GameData gameData = JuggernautClient.getInstance().getPlayerData(player.getUniqueID());

        GameCard card = gameData.getCardData().getCard(null);
        if (card == null){
            // 未选择角色
            drawString(matrixStack, minecraft.fontRenderer, "按下`H`键选择角色", width-80, this.height-20, 0xFFFFFF);
            return;
        }

        int skillFullCoolDown = card.getSkillCoolDown() * card.getSkillUseCount();
        long firstUseSkillTime = gameData.getCardData().chargingFullTime - skillFullCoolDown;
        long timeLeft = now - firstUseSkillTime;
        boolean skillChargingCompleted = gameData.skillUsable(null);
        float skillChargingPer;
        if (timeLeft > skillFullCoolDown){
            skillChargingPer = 1;
        }else {
            skillChargingPer = (float) timeLeft / skillFullCoolDown;
        }

        long timeLeftUltimate = now - gameData.getCardData().lastUseUltimateSkillTime;
        float ultimateSkillChargingPer;
        boolean ultimateSkillChargingCompleted = false;
        if (timeLeftUltimate > card.getUltimateSkillCoolDown()){
            ultimateSkillChargingCompleted = true;
            ultimateSkillChargingPer = 1;
        }else {
            ultimateSkillChargingPer = (float) timeLeftUltimate / card.getUltimateSkillCoolDown();
        }

        this.minecraft.getTextureManager().bindTexture(SKILL_UI);
        matrixStack.push();
        matrixStack.scale(0.35F, 0.35F, 1.0F);

        int baseHeight = (int) (height / 0.35F);
        int baseWidth = (int) (width / 0.35F);

        this.blit(matrixStack, baseWidth-260, baseHeight-43, 0, 0, 240, 24);
        this.blit(matrixStack, baseWidth-260, baseHeight-43, 0, 24, (int)(240F * skillChargingPer), 24);
        this.blit(matrixStack, baseWidth-260, baseHeight-43, 0, 48, 240, 24);

        this.blit(matrixStack, baseWidth-260, baseHeight-73, 0, 0, 240, 24);
        this.blit(matrixStack, baseWidth-260, baseHeight-73, 0, 72, (int)(240F * ultimateSkillChargingPer), 24);
        this.blit(matrixStack, baseWidth-260, baseHeight-73, 0, 48, 240, 24);

        if (skillChargingCompleted){
            this.blit(matrixStack, baseWidth-260, baseHeight-43, 0, 96, 240, 24);
        }

        if (ultimateSkillChargingCompleted){
            this.blit(matrixStack, baseWidth-260, baseHeight-73, 0, 120, 240, 24);
        }

        matrixStack.pop();

        if (!skillChargingCompleted){
            // String time = String.valueOf((int) (card.getSkillCoolDown() - timeLeft) / 1000); 保留两位小数
            String time = String.format("%.2f", (card.getSkillCoolDown() - timeLeft) / 1000F);
            int strWidth = this.minecraft.fontRenderer.getStringWidth(time);
            drawString(matrixStack, minecraft.fontRenderer, time, width-95-strWidth, this.height-15, 0xFFFFFF);
        }

        if (!ultimateSkillChargingCompleted){
            // String time = String.valueOf((int) (card.getUltimateSkillCoolDown() - timeLeftUltimate) / 1000); 保留两位小数
            String time = String.format("%.2f", (card.getUltimateSkillCoolDown() - timeLeftUltimate) / 1000F);
            int strWidth = this.minecraft.fontRenderer.getStringWidth(time);
            drawString(matrixStack, minecraft.fontRenderer, time, width-95-strWidth, this.height-25, 0xFFFFFF);
        }

    }
}
