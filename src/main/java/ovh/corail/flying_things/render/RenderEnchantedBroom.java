package ovh.corail.flying_things.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.flying_things.entity.EntityEnchantedBroom;
import ovh.corail.flying_things.helper.TextureLocation;
import ovh.corail.flying_things.model.ModelEnchantedBroom;

import static ovh.corail.flying_things.helper.Functions.VERTEX_BUILDER_CUTOUT;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("FieldCanBeLocal")
public class RenderEnchantedBroom extends EntityRenderer<EntityEnchantedBroom> {
    private static final ModelEnchantedBroom model = new ModelEnchantedBroom();

    public RenderEnchantedBroom(EntityRendererManager renderManager) {
        super(renderManager);
        this.shadowSize = this.shadowOpaque = 0.2f;
    }

    @Override
    public void render(EntityEnchantedBroom entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int light) {
        render(entity, entityYaw, entity.ticksExisted, partialTicks, matrixStack, iRenderTypeBuffer, light, false);
        super.render(entity, entityYaw, partialTicks, matrixStack, iRenderTypeBuffer, light);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityEnchantedBroom entity) {
        return getTexture(entity);
    }

    private static ResourceLocation getTexture(EntityEnchantedBroom entity) {
        return TextureLocation.TEXTURE_CONCRETE[(entity.getModelType() < TextureLocation.TEXTURE_CONCRETE.length ? entity.getModelType() : 12)];
    }

    @Override
    public boolean shouldRender(EntityEnchantedBroom entity, ClippingHelperImpl camera, double camX, double camY, double camZ) {
        return entity.world == null || super.shouldRender(entity, camera, camX, camY, camZ);
    }

    public static void render(EntityEnchantedBroom entity, float entityYaw, int ticksExisted, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int packedLight, boolean isTEISR) {
        matrixStack.push();

        if (!isTEISR) {
            matrixStack.translate(0d, 0.2d, 0d);
        }
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

        model.render(matrixStack, VERTEX_BUILDER_CUTOUT.apply(iRenderTypeBuffer, getTexture(entity)), packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        if (entity.getHeadType() > 0) {
            matrixStack.push();
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(180f));
            model.head.render(matrixStack, VERTEX_BUILDER_CUTOUT.apply(iRenderTypeBuffer, TextureLocation.TEXTURE_SKULL[entity.getHeadType() - 1]), packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            matrixStack.pop();
        }
        matrixStack.pop();
    }


}
