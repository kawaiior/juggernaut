package github.kawaiior.juggernaut.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.entity.ReviveBeaconEntity;
import github.kawaiior.juggernaut.entity.model.ReviveBeaconModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ReviveBeaconRender extends EntityRenderer<ReviveBeaconEntity> {
    public static final ResourceLocation MAGIC = new ResourceLocation(Juggernaut.MOD_ID, "textures/entity/revive_beacon.png");
    private final EntityModel<ReviveBeaconEntity> reviveBeaconEntityEntityModel;

    public ReviveBeaconRender(EntityRendererManager manager) {
        super(manager);
        reviveBeaconEntityEntityModel = new ReviveBeaconModel();
    }

    @Override
    public void render(ReviveBeaconEntity entity, float entityYaw, float partialTicks,
                       MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        matrixStack.push();

        matrixStack.rotate(Vector3f.YN.rotationDegrees(45));
        IVertexBuilder ivertexbuilder = buffer.getBuffer(this.reviveBeaconEntityEntityModel.getRenderType(this.getEntityTexture(entity)));
        this.reviveBeaconEntityEntityModel.render(matrixStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();

        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getEntityTexture(ReviveBeaconEntity reviveBeaconEntity) {
        return MAGIC;
    }
}
