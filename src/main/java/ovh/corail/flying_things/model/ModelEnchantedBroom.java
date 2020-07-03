package ovh.corail.flying_things.model;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.flying_things.entity.EntityEnchantedBroom;

@OnlyIn(Dist.CLIENT)
public class ModelEnchantedBroom<T extends EntityEnchantedBroom> extends EntityModel<T> {
    public final RendererModel stick;
    public final RendererModel head;
    private final RendererModel NW;
    private final RendererModel SW;
    private final RendererModel SE;
    private final RendererModel NE;
    private final RendererModel NW_1;
    private final RendererModel SW_1;
    private final RendererModel SE_1;
    private final RendererModel NE_1;

    public ModelEnchantedBroom() {
        textureWidth = 32;
        textureHeight = 32;
        SE_1 = new RendererModel(this, 0, 0);
        SE_1.setRotationPoint(-0.5F, -0.25F, 4.0F);
        SE_1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 10, 0.0F);
        setRotateAngle(SE_1, -0.17453292519943295F, -0.17453292519943295F, 0.0F);
        SW_1 = new RendererModel(this, 0, 0);
        SW_1.setRotationPoint(-0.5F, -0.25F, 4.0F);
        SW_1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 10, 0.0F);
        setRotateAngle(SW_1, -0.17453292519943295F, 0.17453292519943295F, 0.0F);
        SE = new RendererModel(this, 0, 0);
        SE.setRotationPoint(-0.5F, 0.0F, 4.0F);
        SE.addBox(0.0F, 0.0F, 0.0F, 1, 1, 10, 0.0F);
        setRotateAngle(SE, -0.3490658503988659F, -0.3490658503988659F, 0.0F);
        NE_1 = new RendererModel(this, 0, 0);
        NE_1.setRotationPoint(-0.5F, -0.25F, 4.0F);
        NE_1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 10, 0.0F);
        setRotateAngle(NE_1, 0.17453292519943295F, -0.17453292519943295F, 0.0F);
        NW = new RendererModel(this, 0, 0);
        NW.setRotationPoint(0.0F, -0.5F, 4.0F);
        NW.addBox(0.0F, 0.0F, 0.0F, 1, 1, 10, 0.0F);
        setRotateAngle(NW, 0.3490658503988659F, 0.3490658503988659F, 0.0F);
        stick = new RendererModel(this, -2, 7);
        stick.setRotationPoint(-0.5F, -0.5F, -20.0F);
        stick.addBox(0.0F, 0.0F, 0.0F, 1, 1, 24, 0.0F);
        SW = new RendererModel(this, 0, 0);
        SW.setRotationPoint(0.0F, 0.0F, 4.0F);
        SW.addBox(0.0F, 0.0F, 0.0F, 1, 1, 10, 0.0F);
        setRotateAngle(SW, -0.3490658503988659F, 0.3490658503988659F, 0.0F);
        NE = new RendererModel(this, 0, 0);
        NE.setRotationPoint(-0.5F, -0.5F, 4.0F);
        NE.addBox(0.0F, 0.0F, 0.0F, 1, 1, 10, 0.0F);
        setRotateAngle(NE, 0.3490658503988659F, -0.3490658503988659F, 0.0F);
        NW_1 = new RendererModel(this, 0, 0);
        NW_1.setRotationPoint(-0.5F, -0.25F, 4.0F);
        NW_1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 10, 0.0F);
        setRotateAngle(NW_1, 0.17453292519943295F, 0.17453292519943295F, 0.0F);
        textureWidth = 16;
        textureHeight = 16;
        head = new RendererModel(this, 0, 0);
        head.setRotationPoint(-2f, -2f, 0f);
        head.addBox(0.0F, 0.0F, -24.0F, 4, 4, 4, 0f);
    }

    @Override
    public void render(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        //setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        SE_1.render(scale);
        SW_1.render(scale);
        SE.render(scale);
        NE_1.render(scale);
        NW.render(scale);
        SW.render(scale);
        NE.render(scale);
        NW_1.render(scale);
    }

    private void setRotateAngle(RendererModel modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
