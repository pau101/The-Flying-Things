package ovh.corail.flying_things.helper;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.BiFunction;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class Functions {
    private static final Function<ResourceLocation, RenderType> FLYING_THINGS_GLINT = rl -> RenderType.makeType("flying_things_glint", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, 7, 256, RenderType.State.getBuilder().texture(new RenderState.TextureState(rl, true, false)).writeMask(new RenderState.WriteMaskState(true, false)).depthTest(new RenderState.DepthTestState("==", 514)).transparency(new RenderState.TransparencyState("glint_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ONE);
        RenderSystem.enableCull();
    }, () -> {
        RenderSystem.disableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    })).texturing(new RenderState.TexturingState("glint_texturing", () -> {
        setupGlintTexturing(1f);
    }, () -> {
        RenderSystem.matrixMode(5890);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
    })).build(false));

    private static void setupGlintTexturing(float scaleIn) {
        RenderSystem.matrixMode(5890);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        long i = Util.milliTime() * 8L;
        float f = (float)(i % 110000L) / 110000f;
        float f1 = (float)(i % 30000L) / 30000f;
        RenderSystem.translatef(-f, f1, 0f);
        RenderSystem.rotatef(10f, 0f, 0f, 1f);
        RenderSystem.scalef(scaleIn, scaleIn, scaleIn);
        RenderSystem.matrixMode(5888);
    }

    public static final BiFunction<IRenderTypeBuffer, ResourceLocation, IVertexBuilder> VERTEX_BUILDER_CUTOUT = (iRenderTypeBuffer, rl) -> iRenderTypeBuffer.getBuffer(RenderType.getEntityCutout(rl));
    private static final BiFunction<IRenderTypeBuffer, ResourceLocation, IVertexBuilder> VERTEX_BUILDER_GLINTER = (iRenderTypeBuffer, rl) -> iRenderTypeBuffer.getBuffer(FLYING_THINGS_GLINT.apply(rl));
    public static final Function<IRenderTypeBuffer, IVertexBuilder> VERTEX_BUILDER_GLINT = iRenderTypeBuffer -> VERTEX_BUILDER_GLINTER.apply(iRenderTypeBuffer, TextureLocation.TEXTURE_EFFECT);
}
