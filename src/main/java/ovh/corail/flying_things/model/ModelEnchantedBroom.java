package ovh.corail.flying_things.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.entity.EntityEnchantedBroom;
import ovh.corail.flying_things.helper.Functions;
import ovh.corail.flying_things.helper.TextureLocation;

@OnlyIn(Dist.CLIENT)
public class ModelEnchantedBroom extends SegmentedModel<EntityEnchantedBroom> {
    private final ModelRenderer stick;
    public final ModelRenderer head;
    private final ModelRenderer NW;
    private final ModelRenderer SW;
    private final ModelRenderer SE;
    private final ModelRenderer NE;
    private final ModelRenderer NW_1;
    private final ModelRenderer SW_1;
    private final ModelRenderer SE_1;
    private final ModelRenderer NE_1;
    private final ImmutableList<ModelRenderer> modelList;

    public ModelEnchantedBroom() {
        textureWidth = 32;
        textureHeight = 32;
        SE_1 = new ModelRenderer(this, 0, 0);
        SE_1.setRotationPoint(-0.5f, -0.25f, 4f);
        SE_1.addBox(0f, 0f, 0f, 1, 1, 10, 0f);
        setRotateAngle(SE_1, -0.17453292519943295f, -0.17453292519943295f, 0f);
        SW_1 = new ModelRenderer(this, 0, 0);
        SW_1.setRotationPoint(-0.5f, -0.25f, 4f);
        SW_1.addBox(0f, 0f, 0f, 1, 1, 10, 0f);
        setRotateAngle(SW_1, -0.17453292519943295f, 0.17453292519943295f, 0f);
        SE = new ModelRenderer(this, 0, 0);
        SE.setRotationPoint(-0.5f, 0f, 4f);
        SE.addBox(0f, 0f, 0f, 1, 1, 10, 0f);
        setRotateAngle(SE, -0.3490658503988659f, -0.3490658503988659f, 0f);
        NE_1 = new ModelRenderer(this, 0, 0);
        NE_1.setRotationPoint(-0.5f, -0.25f, 4f);
        NE_1.addBox(0f, 0f, 0f, 1, 1, 10, 0f);
        setRotateAngle(NE_1, 0.17453292519943295f, -0.17453292519943295f, 0f);
        NW = new ModelRenderer(this, 0, 0);
        NW.setRotationPoint(0f, -0.5f, 4f);
        NW.addBox(0f, 0f, 0f, 1, 1, 10, 0f);
        setRotateAngle(NW, 0.3490658503988659f, 0.3490658503988659f, 0f);
        stick = new ModelRenderer(this, -2, 7);
        stick.setRotationPoint(-0.5f, -0.5f, -20f);
        stick.addBox(0f, 0f, 0f, 1, 1, 24, 0f);
        SW = new ModelRenderer(this, 0, 0);
        SW.setRotationPoint(0f, 0f, 4f);
        SW.addBox(0f, 0f, 0f, 1, 1, 10, 0f);
        setRotateAngle(SW, -0.3490658503988659f, 0.3490658503988659f, 0f);
        NE = new ModelRenderer(this, 0, 0);
        NE.setRotationPoint(-0.5f, -0.5f, 4f);
        NE.addBox(0f, 0f, 0f, 1, 1, 10, 0f);
        setRotateAngle(NE, 0.3490658503988659f, -0.3490658503988659f, 0f);
        NW_1 = new ModelRenderer(this, 0, 0);
        NW_1.setRotationPoint(-0.5f, -0.25f, 4f);
        NW_1.addBox(0f, 0f, 0f, 1, 1, 10, 0f);
        setRotateAngle(NW_1, 0.17453292519943295f, 0.17453292519943295f, 0f);
        head = new ModelRenderer(16, 16, 0, 0);
        head.setRotationPoint(-2f, -2f, 0f);
        head.addBox(0f, 0f, -24f, 4, 4, 4, 0f);
        ImmutableList.Builder<ModelRenderer> builder = ImmutableList.builder();
        builder.add(SE_1, SW_1, SE, NE_1, NW, SW, NE, NW_1, stick); //head
        this.modelList = builder.build();
    }

    @Override
    public void setRotationAngles(EntityEnchantedBroom entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.stick.render(matrixStack, iVertexBuilder, packedLight, packedOverlay, red, green, blue, alpha);
        IRenderTypeBuffer.Impl iRenderTypeBuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder brushVertexBuilder = Functions.VERTEX_BUILDER_CUTOUT.apply(iRenderTypeBuffer, TextureLocation.TEXTURE_HAY);
        getParts().forEach(part -> {
            if (part != this.stick) {
                part.render(matrixStack, brushVertexBuilder, packedLight, packedOverlay, red, green, blue, alpha);
            }
        });
        if (ConfigFlyingThings.client.renderEffect.get()) {
            IVertexBuilder ivertexbuilder2 = Functions.VERTEX_BUILDER_GLINT.apply(iRenderTypeBuffer);
            getParts().forEach(part -> part.render(matrixStack, ivertexbuilder2, packedLight, packedOverlay, red, green, blue, alpha));
        }
    }

    private void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return this.modelList;
    }
}
