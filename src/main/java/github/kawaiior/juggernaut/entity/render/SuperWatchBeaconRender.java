package github.kawaiior.juggernaut.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.entity.SuperWatchBeaconEntity;
import github.kawaiior.juggernaut.entity.WatchBeaconEntity;
import github.kawaiior.juggernaut.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

public class SuperWatchBeaconRender extends EntityRenderer<SuperWatchBeaconEntity> {
    public static final ResourceLocation MAGIC = new ResourceLocation(Juggernaut.MOD_ID, "textures/entity/magic.png");

    public SuperWatchBeaconRender(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void render(SuperWatchBeaconEntity entity, float entityYaw, float partialTicks,
                       MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        matrixStack.push();
        Matrix4f mat = matrixStack.getLast().getMatrix();
        RenderUtil.renderImageOnEntityRender(buffer, mat, RenderUtil.magicRender, 24F, 0, 132/255F, 1, 1F);
        matrixStack.pop();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getEntityTexture(SuperWatchBeaconEntity reviveBeaconEntity) {
        return MAGIC;
    }
}
