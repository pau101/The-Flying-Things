package ovh.corail.flying_things.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import ovh.corail.flying_things.ConfigFlyingThings;
import ovh.corail.flying_things.entity.EntityMagicCarpet;
import ovh.corail.flying_things.helper.TextureLocation;

@OnlyIn(Dist.CLIENT)
public final class RenderMagicCarpet<T extends EntityMagicCarpet> extends EntityRenderer<T> {
    private static final float TOP_MIN_U = 0f, TOP_MIN_V = 0f, TOP_MAX_U = 1f, TOP_MAX_V = 1f;
    private static final float BOTTOM_MIN_U = 0f, BOTTOM_MIN_V = 0f, BOTTOM_MAX_U = 1f, BOTTOM_MAX_V = 1f;
    private static final int FACE_COUNT = 16, VERTEX_COUNT = FACE_COUNT + 1;
    private static final double WIDTH = 1.4d, LENGTH = WIDTH * 1.5d;
    private static final double X1 = WIDTH / 2d, X0 = -WIDTH / 2d;
    private static final double[] VERT_Y = new double[VERTEX_COUNT];
    private static final double[] VERT_Z = new double[VERTEX_COUNT];

    //private static final Set<UUID> playersList = new HashSet<>();
    //private static boolean isLast = false;

    public RenderMagicCarpet(EntityRendererManager manager) {
        super(manager);
        this.shadowSize = this.shadowOpaque = 0.2f;
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float yaw, float partialTicks) {
        //Entity passenger = entity.getControllingPassenger();
        //if (!(passenger instanceof EntityPlayer) || (passenger == Minecraft.getInstance().player && Minecraft.getInstance().gameSettings.thirdPersonView == 0)) {
        render(getRenderManager(), entity, x, y, z, yaw, entity.ticksExisted, partialTicks, false);
        //}
        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return getTexture(entity);
    }

    private static ResourceLocation getTexture(EntityMagicCarpet entity) {
        return TextureLocation.TEXTURE_CARPET[(entity.getModelType() < TextureLocation.TEXTURE_CARPET.length ? entity.getModelType() : 0)];
    }

    public static void render(EntityRendererManager renderManager, EntityMagicCarpet entity, double x, double y, double z, float yaw, int ticksExisted, float partialTicks, boolean useMask) {
        float ageInTicks = ticksExisted + partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x, (float) y - 0.7f, (float) z);
        GlStateManager.rotatef(180f - yaw, 0f, 1f, 0f);

        if (entity.world != null) { // checks if !isItemStack
            float f = (float) entity.getTimeSinceHit() - partialTicks;
            float f1 = entity.getDamageTaken() - partialTicks;
            if (f1 < 0f) {
                f1 = 0f;
            }
            if (f > 0f) {
                GlStateManager.rotatef(MathHelper.sin(f) * f * f1 / 10f * (float) entity.getForwardDirection(), 1f, 0f, 0f);
            }
        }
        GlStateManager.enableCull();
        renderManager.textureManager.bindTexture(getTexture(entity));

        for (int i = 0; i < VERTEX_COUNT; i++) {
            VERT_Y[i] = MathHelper.sin(ageInTicks * 0.1F + i * 0.25F) * 0.065D + 0.95D;
            VERT_Z[i] = (double) i / FACE_COUNT * LENGTH - LENGTH / 2;
        }
        // TODO options for brightness in dark
        //OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, 240f, 240f);
        //GlStateManager.disableLighting();
        drawCarpet();
        //GlStateManager.enableLighting();
        //OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, OpenGlHelper.lastBrightnessX, OpenGlHelper.lastBrightnessY);
        renderLayer(renderManager, entity, ticksExisted, partialTicks);
        //if (useMask) { drawMask(buf, ageInTicks); }

        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
    }

    private static void renderLayer(EntityRendererManager renderManager, EntityMagicCarpet carpet, int ticksExisted, float partialTicks) {
        if (!ConfigFlyingThings.client.renderEffect.get()) {
            return;
        }
        float ageInTicks = ticksExisted + partialTicks;
        GlStateManager.pushMatrix();
        boolean flag = !carpet.isInvisible();
        GlStateManager.depthMask(!flag);
        renderManager.textureManager.bindTexture(TextureLocation.TEXTURE_EFFECT);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(ageInTicks * 0.01f, ageInTicks * 0.01f, 0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        GlStateManager.color4f(0.2f, 0.2f, 0.2f, 0.2f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

        Minecraft.getInstance().gameRenderer.setupFogColor(true);
        drawCarpet();
        Minecraft.getInstance().gameRenderer.setupFogColor(false);

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(flag);

        GlStateManager.color4f(1f, 1f, 1f, 1f);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.popMatrix();
    }

    private static void drawCarpet() {
        BufferBuilder buf = Tessellator.getInstance().getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

        for (int i = 0; i < FACE_COUNT; i++) {
            float t0 = (float) i / FACE_COUNT;
            float t1 = (float) (i + 1) / FACE_COUNT;
            double y0 = VERT_Y[i], y1 = VERT_Y[i + 1];
            double z0 = VERT_Z[i], z1 = VERT_Z[i + 1];
            // up top
            float v00 = TOP_MIN_V + (TOP_MAX_V - TOP_MIN_V) * t0;
            float v01 = TOP_MIN_V + (TOP_MAX_V - TOP_MIN_V) * t1;
            buf.pos(X0, y0, z0).tex(TOP_MIN_U, v00).normal(0.0F, 1.0F, 0.0F).endVertex();
            buf.pos(X0, y1, z1).tex(TOP_MIN_U, v01).normal(0.0F, 1.0F, 0.0F).endVertex();
            buf.pos(X1, y1, z1).tex(TOP_MAX_U, v01).normal(0.0F, 1.0F, 0.0F).endVertex();
            buf.pos(X1, y0, z0).tex(TOP_MAX_U, v00).normal(0.0F, 1.0F, 0.0F).endVertex();
            // up bottom
            float v10 = BOTTOM_MIN_V + (BOTTOM_MAX_V - BOTTOM_MIN_V) * t0;
            float v11 = BOTTOM_MIN_V + (BOTTOM_MAX_V - BOTTOM_MIN_V) * t1;
            buf.pos(X0, y1, z1).tex(BOTTOM_MIN_U, v11).normal(0.0F, -1.0F, 0.0F).endVertex();
            buf.pos(X0, y0, z0).tex(BOTTOM_MIN_U, v10).normal(0.0F, -1.0F, 0.0F).endVertex();
            buf.pos(X1, y0, z0).tex(BOTTOM_MAX_U, v10).normal(0.0F, -1.0F, 0.0F).endVertex();
            buf.pos(X1, y1, z1).tex(BOTTOM_MAX_U, v11).normal(0.0F, -1.0F, 0.0F).endVertex();
        }
        Tessellator.getInstance().draw();
    }
}
