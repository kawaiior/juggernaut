package github.kawaiior.juggernaut.util;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.mixin.AccessorRenderState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;

public class RenderUtil {
    public static final ResourceLocation MAGIC = new ResourceLocation(Juggernaut.MOD_ID, "textures/entity/magic.png");

    public static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = AccessorRenderState.getTranslucentTransparency();
    public static final RenderState.TargetState itemTarget = AccessorRenderState.getItemEntityTarget();
    public static final RenderState.CullState disableCull = new RenderState.CullState(false);
    public static final RenderState.ShadeModelState smoothShade = new RenderState.ShadeModelState(true);
    public static final RenderState.LightmapState enableLightmap = new RenderState.LightmapState(true);

    public static final RenderState.TextureState magicTexture = new RenderState.TextureState(MAGIC, false, true);
    public static final RenderType.State glState = RenderType.State.getBuilder().texture(magicTexture)
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .target(itemTarget)
            .cull(disableCull)
            .shadeModel(smoothShade)
            .alpha(new RenderState.AlphaState(0.05F))
            .lightmap(enableLightmap).build(true);

    public static final RenderType magicRender = RenderType.makeType(
            "revive_beacon",
            DefaultVertexFormats.POSITION_COLOR_TEX,
            GL11.GL_QUADS,
            64,
            glState
    );

    public static void renderImageOnEntityRender(IRenderTypeBuffer buffer, Matrix4f mat, RenderType renderType,
                                          float size, float r, float g, float b, float alpha) {
        IVertexBuilder iVertexBuilder = buffer.getBuffer(renderType);
        iVertexBuilder.pos(mat, -1*size, 0, -1*size)
                .color(r, g, b, alpha)
                .tex(0, 0).endVertex();
        iVertexBuilder.pos(mat, -1*size, 0, 1*size)
                .color(r, g, b, alpha)
                .tex(0, 1).endVertex();
        iVertexBuilder.pos(mat, 1*size, 0, 1*size)
                .color(r, g, b, alpha)
                .tex(1, 1).endVertex();
        iVertexBuilder.pos(mat, 1*size, 0, -1*size)
                .color(r, g, b, alpha)
                .tex(1, 0).endVertex();
    }

}
