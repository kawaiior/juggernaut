package github.kawaiior.juggernaut.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.entity.PaintBubbleEntity;
import github.kawaiior.juggernaut.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

public class PaintBubbleRender extends EntityRenderer<PaintBubbleEntity> {

    public static final ResourceLocation MAGIC = new ResourceLocation(Juggernaut.MOD_ID, "textures/entity/magic.png");

    public PaintBubbleRender(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(PaintBubbleEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        if (entity.isHit()) {
            matrixStack.push();
            Matrix4f mat = matrixStack.getLast().getMatrix();
            RenderUtil.renderImageOnEntityRender(buffer, mat, RenderUtil.magicRender, 4F, 216/255F, 123/255F, 32/255F, 1F);
            matrixStack.pop();
            super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        } else {
            // TODO RENDER BALL
            matrixStack.push();
            Matrix4f mat = matrixStack.getLast().getMatrix();
            RenderUtil.renderImageOnEntityRender(buffer, mat, RenderUtil.magicRender, 0.75F, 216/255F, 123/255F, 32/255F, 1F);
            matrixStack.pop();
            super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        }
    }

    @Override
    public ResourceLocation getEntityTexture(PaintBubbleEntity entity) {
        return MAGIC;
    }
}
