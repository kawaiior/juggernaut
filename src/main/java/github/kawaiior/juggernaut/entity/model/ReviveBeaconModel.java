package github.kawaiior.juggernaut.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import github.kawaiior.juggernaut.entity.ReviveBeaconEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ReviveBeaconModel extends EntityModel<ReviveBeaconEntity> {
    private final ModelRenderer bb_main;

    public ReviveBeaconModel() {
        textureWidth = 16;
        textureHeight = 16;

        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
        bb_main.setTextureOffset(0, 0).addBox(-2.0F, 2.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        bb_main.setTextureOffset(0, 5).addBox(-1.0F, 3.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        bb_main.setTextureOffset(0, 8).addBox(1.0F, 1.0F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        bb_main.setTextureOffset(4, 8).addBox(1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        bb_main.setTextureOffset(8, 5).addBox(-2.0F, 1.0F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        bb_main.setTextureOffset(8, 8).addBox(-2.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        bb_main.setTextureOffset(0, 11).addBox(-3.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        bb_main.setTextureOffset(4, 11).addBox(-2.0F, 0.0F, 2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        bb_main.setTextureOffset(8, 11).addBox(-2.0F, 0.0F, -3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        bb_main.setTextureOffset(12, 5).addBox(-3.0F, 0.0F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        bb_main.setTextureOffset(12, 7).addBox(1.0F, 0.0F, -3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        bb_main.setTextureOffset(12, 9).addBox(2.0F, 0.0F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        bb_main.setTextureOffset(12, 11).addBox(1.0F, 0.0F, 2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        bb_main.setTextureOffset(0, 13).addBox(2.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(ReviveBeaconEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}

