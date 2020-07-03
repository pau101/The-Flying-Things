package ovh.corail.flying_things.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.entity.EntityMagicCarpet;
import ovh.corail.flying_things.helper.TextureLocation;

import static ovh.corail.flying_things.helper.Functions.VERTEX_BUILDER_CUTOUT;
import static ovh.corail.flying_things.helper.Functions.VERTEX_BUILDER_GLINT;

@OnlyIn(Dist.CLIENT)
public final class RenderMagicCarpet extends EntityRenderer<EntityMagicCarpet> {
    private static final float TOP_MIN_U = 0f, TOP_MIN_V = 0f, TOP_MAX_U = 1f, TOP_MAX_V = 1f;
    private static final float BOTTOM_MIN_U = 0f, BOTTOM_MIN_V = 0f, BOTTOM_MAX_U = 1f, BOTTOM_MAX_V = 1f;
    private static final int FACE_COUNT = 16, VERTEX_COUNT = FACE_COUNT + 1;
    private static final float WIDTH = 1.4f, LENGTH = WIDTH * 1.5f;
    private static final float X1 = WIDTH / 2f, X0 = -WIDTH / 2f;
    private static final float[] VERT_Y = new float[VERTEX_COUNT];
    private static final float[] VERT_Z = new float[VERTEX_COUNT];

    public RenderMagicCarpet(EntityRendererManager manager) {
        super(manager);
        this.shadowSize = this.shadowOpaque = 0.2f;
    }

    @Override
    public void render(EntityMagicCarpet entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int packedLight) {
        render(entity, entityYaw, entity.ticksExisted, partialTicks, matrixStack, iRenderTypeBuffer, packedLight, false);
        super.render(entity, entityYaw, partialTicks, matrixStack, iRenderTypeBuffer, packedLight);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityMagicCarpet entity) {
        return getTexture(entity);
    }

    private static ResourceLocation getTexture(EntityMagicCarpet entity) {
        return TextureLocation.TEXTURE_CARPET[(entity.getModelType() < TextureLocation.TEXTURE_CARPET.length ? entity.getModelType() : 0)];
    }

    @Override
    public boolean shouldRender(EntityMagicCarpet entity, ClippingHelperImpl camera, double camX, double camY, double camZ) {
        return entity.world == null || super.shouldRender(entity, camera, camX, camY, camZ);
    }

    public static void render(EntityMagicCarpet entity, float entityYaw, int ticksExisted, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int packedLight, boolean isTEISR) {
        matrixStack.push();
        matrixStack.translate(0f, -0.7f, 0f);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(180f - entityYaw));

        if (!isTEISR) {
            float f = (float) entity.getTimeSinceHit() - partialTicks;
            float f1 = entity.getDamageTaken() - partialTicks;
            if (f1 < 0f) {
                f1 = 0f;
            }
            if (f > 0f) {
                matrixStack.rotate(Vector3f.XP.rotationDegrees(MathHelper.sin(f) * f * f1 / 10f * (float) entity.getForwardDirection()));
            }
        }

        float ageInTicks = ticksExisted + partialTicks;
        for (int i = 0; i < VERTEX_COUNT; i++) {
            VERT_Y[i] = MathHelper.sin(ageInTicks * 0.1f + i * 0.25f) * 0.065f + 0.95f;
            VERT_Z[i] = (float) i / FACE_COUNT * LENGTH - LENGTH / 2;
        }

        drawCarpet(matrixStack, VERTEX_BUILDER_CUTOUT.apply(iRenderTypeBuffer, getTexture(entity)), packedLight);

        if (ConfigFlyingThings.client.renderEffect.get()) {
            drawCarpet(matrixStack, VERTEX_BUILDER_GLINT.apply(iRenderTypeBuffer), packedLight);
        }

        matrixStack.pop();
    }

    private static void drawCarpet(MatrixStack matrixStack, IVertexBuilder buf, int packedLight) {
        MatrixStack.Entry last = matrixStack.getLast();
        Matrix4f matrix4f = last.getMatrix();
        Matrix3f matrixNormal = last.getNormal();

        for (int i = 0; i < FACE_COUNT; i++) {
            float t0 = (float) i / FACE_COUNT;
            float t1 = (float) (i + 1) / FACE_COUNT;
            float y0 = VERT_Y[i], y1 = VERT_Y[i + 1];
            float z0 = VERT_Z[i], z1 = VERT_Z[i + 1];
            // up top
            float v00 = TOP_MIN_V + (TOP_MAX_V - TOP_MIN_V) * t0;
            float v01 = TOP_MIN_V + (TOP_MAX_V - TOP_MIN_V) * t1;
            buf.pos(matrix4f, X0, y0, z0).color(1f, 1f, 1f, 1f).tex(TOP_MIN_U, v00).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrixNormal, 0f, 1f, 0f).endVertex();
            buf.pos(matrix4f, X0, y1, z1).color(1f, 1f, 1f, 1f).tex(TOP_MIN_U, v01).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrixNormal, 0f, 1f, 0f).endVertex();
            buf.pos(matrix4f, X1, y1, z1).color(1f, 1f, 1f, 1f).tex(TOP_MAX_U, v01).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrixNormal, 0f, 1f, 0f).endVertex();
            buf.pos(matrix4f, X1, y0, z0).color(1f, 1f, 1f, 1f).tex(TOP_MAX_U, v00).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrixNormal, 0f, 1f, 0f).endVertex();
            // up bottom
            float v10 = BOTTOM_MIN_V + (BOTTOM_MAX_V - BOTTOM_MIN_V) * t0;
            float v11 = BOTTOM_MIN_V + (BOTTOM_MAX_V - BOTTOM_MIN_V) * t1;
            buf.pos(matrix4f, X0, y1, z1).color(1f, 1f, 1f, 1f).tex(BOTTOM_MIN_U, v11).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrixNormal, 0f, -1f, 0f).endVertex();
            buf.pos(matrix4f, X0, y0, z0).color(1f, 1f, 1f, 1f).tex(BOTTOM_MIN_U, v10).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrixNormal, 0f, -1f, 0f).endVertex();
            buf.pos(matrix4f, X1, y0, z0).color(1f, 1f, 1f, 1f).tex(BOTTOM_MAX_U, v10).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrixNormal, 0f, -1f, 0f).endVertex();
            buf.pos(matrix4f, X1, y1, z1).color(1f, 1f, 1f, 1f).tex(BOTTOM_MAX_U, v11).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrixNormal, 0f, -1f, 0f).endVertex();
        }
    }
}
