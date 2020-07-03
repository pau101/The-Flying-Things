package ovh.corail.flying_things.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.flying_things.ConfigFlyingThings;
import ovh.corail.flying_things.entity.EntityEnchantedBroom;
import ovh.corail.flying_things.helper.TextureLocation;
import ovh.corail.flying_things.model.ModelEnchantedBroom;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("FieldCanBeLocal")
public class RenderEnchantedBroom<T extends EntityEnchantedBroom> extends EntityRenderer<T> {
    private final ModelEnchantedBroom<T> model = new ModelEnchantedBroom<>();
    private final float scale = 0.0625f;

    public RenderEnchantedBroom(EntityRendererManager renderManager) {
        super(renderManager);
        this.shadowSize = this.shadowOpaque = 0.2f;
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return TextureLocation.TEXTURE_CONCRETE[(entity.getModelType() < TextureLocation.TEXTURE_CONCRETE.length ? entity.getModelType() : 12)];
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        renderBroom(entity, x, y, z, entityYaw, partialTicks, false);
    }

    @Override
    public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ) {
        return true;
    }

    void renderBroom(T entity, double x, double y, double z, float entityYaw, float partialTicks, boolean isItemStack) {
        float ageInTicks, rotationYawHead, rotationYaw, rotationPitch;
        if (isItemStack) {
            ageInTicks = Minecraft.getInstance().player.ticksExisted + partialTicks;
            rotationYawHead = rotationYaw = rotationPitch = 0f;
        } else {
            ageInTicks = entity.ticksExisted + partialTicks;
            rotationYawHead = entity.getRotationYawHead();
            rotationYaw = entity.rotationYaw;
            rotationPitch = 0f;
        }
        float limbSwing = ageInTicks / 80f;

        GlStateManager.pushMatrix();
        GlStateManager.enableCull();

        if (!isItemStack) {
            GlStateManager.translated(x, y + 0.2d, z);
        }
        GlStateManager.rotatef(180f - entityYaw, 0f, 1f, 0f);
        if (!isItemStack) {
            float f = (float) entity.getTimeSinceHit() - partialTicks;
            float f1 = entity.getDamageTaken() - partialTicks;
            if (f1 < 0f) {
                f1 = 0f;
            }
            if (f > 0f) {
                GlStateManager.rotatef(MathHelper.sin(f) * f * f1 / 10f * (float) entity.getForwardDirection(), 1f, 0f, 0f);
            }
        }

        GlStateManager.pushMatrix();
        bindTexture(TextureLocation.TEXTURE_HAY);
        model.render(entity, limbSwing, 0f, ageInTicks, rotationYawHead, rotationPitch, scale);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        bindEntityTexture(entity);
        model.stick.render(scale);
        GlStateManager.popMatrix();

        if (entity.getHeadType() > 0) {
            GlStateManager.pushMatrix();
            bindTexture(TextureLocation.TEXTURE_SKULL[entity.getHeadType() - 1]);
            GlStateManager.rotatef(180f, 0f, 0f, 1f);
            model.head.render(scale);
            GlStateManager.popMatrix();
        }

        doRenderLayer(entity, limbSwing, 0f, ageInTicks, rotationYawHead, rotationPitch);
        GlStateManager.disableCull();
        GlStateManager.popMatrix();
        if (!isItemStack) {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    private void doRenderLayer(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!ConfigFlyingThings.client.renderEffect.get()) {
            return;
        }
        GlStateManager.pushMatrix();
        boolean flag = !entity.isInvisible();
        GlStateManager.depthMask(!flag);
        bindTexture(TextureLocation.TEXTURE_EFFECT);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(ageInTicks * 0.01f, ageInTicks * 0.01f, 0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        GlStateManager.color4f(0.5f, 0.5f, 0.5f, 1f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        this.model.setModelAttributes(this.model);

        Minecraft.getInstance().gameRenderer.setupFogColor(true);
        this.model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, this.scale);
        this.model.stick.render(this.scale);
        Minecraft.getInstance().gameRenderer.setupFogColor(false);

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        //GlStateManager.disableBlend();
        GlStateManager.depthMask(flag);

        GlStateManager.color4f(1f, 1f, 1f, 1f);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.popMatrix();
    }
}
