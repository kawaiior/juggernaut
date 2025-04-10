package github.kawaiior.juggernaut.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.entity.ElectricBallEntity;
import github.kawaiior.juggernaut.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ElectricBallRender extends EntityRenderer<ElectricBallEntity> {
    public static final ResourceLocation MAGIC = new ResourceLocation(Juggernaut.MOD_ID, "textures/entity/magic.png");

    public ElectricBallRender(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void render(ElectricBallEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        float rotationSpeed = 7f;
        float rotationAngle = entity.ticksExisted * rotationSpeed;

        matrixStack.push();
        matrixStack.rotate(Vector3f.XP.rotationDegrees(rotationAngle));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(rotationAngle));
        RenderUtil.renderImageOnEntityRender(buffer, matrixStack.getLast().getMatrix(), RenderUtil.magicRender, 4F, 216/255F, 123/255F, 32/255F, 1F);
        matrixStack.pop();

        matrixStack.push();
        matrixStack.rotate(Vector3f.XP.rotationDegrees(90+rotationAngle));
        RenderUtil.renderImageOnEntityRender(buffer, matrixStack.getLast().getMatrix(), RenderUtil.magicRender, 4F, 216/255F, 123/255F, 32/255F, 1F);
        matrixStack.pop();

        matrixStack.push();
        matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(90+rotationAngle));
        RenderUtil.renderImageOnEntityRender(buffer, matrixStack.getLast().getMatrix(), RenderUtil.magicRender, 4F, 216/255F, 123/255F, 32/255F, 1F);
        matrixStack.pop();

        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getEntityTexture(ElectricBallEntity electricBallEntity) {
        return MAGIC;
    }
}
