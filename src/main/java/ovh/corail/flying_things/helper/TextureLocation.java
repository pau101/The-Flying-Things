package ovh.corail.flying_things.helper;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class TextureLocation {
    public static final ResourceLocation[] TEXTURE_CONCRETE = {
            new ResourceLocation("minecraft", "textures/block/white_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/orange_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/magenta_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/light_blue_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/yellow_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/lime_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/pink_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/gray_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/light_gray_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/cyan_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/purple_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/blue_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/brown_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/green_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/red_concrete.png"),
            new ResourceLocation("minecraft", "textures/block/black_concrete.png")
    };
    public static final ResourceLocation[] TEXTURE_CARPET = {
            new ResourceLocation(MOD_ID, "textures/entity/carpet_0.png"),
            new ResourceLocation(MOD_ID, "textures/entity/carpet_1.png"),
            new ResourceLocation(MOD_ID, "textures/entity/carpet_2.png"),
            new ResourceLocation(MOD_ID, "textures/entity/carpet_3.png"),
            new ResourceLocation(MOD_ID, "textures/entity/carpet_4.png"),
            new ResourceLocation(MOD_ID, "textures/entity/carpet_5.png"),
            new ResourceLocation(MOD_ID, "textures/entity/carpet_6.png"),
            new ResourceLocation(MOD_ID, "textures/entity/carpet_7.png"),
            new ResourceLocation(MOD_ID, "textures/entity/carpet_8.png"),
            new ResourceLocation(MOD_ID, "textures/entity/halloween_0.png"), // 9
            new ResourceLocation(MOD_ID, "textures/entity/halloween_1.png"),
            new ResourceLocation(MOD_ID, "textures/entity/halloween_2.png"),
            new ResourceLocation(MOD_ID, "textures/entity/halloween_3.png"),
            new ResourceLocation(MOD_ID, "textures/entity/halloween_4.png"),
            new ResourceLocation(MOD_ID, "textures/entity/ocean_0.png"), // 14
            new ResourceLocation(MOD_ID, "textures/entity/ocean_1.png"),
            new ResourceLocation(MOD_ID, "textures/entity/ocean_2.png"),
            new ResourceLocation(MOD_ID, "textures/entity/ocean_3.png"),
            new ResourceLocation(MOD_ID, "textures/entity/pillage_0.png"), // 18
            new ResourceLocation(MOD_ID, "textures/entity/pillage_1.png")
    };
    public static final ResourceLocation[] TEXTURE_SKULL = {
            new ResourceLocation(MOD_ID, "textures/entity/head_pumpkin.png"),
            new ResourceLocation(MOD_ID, "textures/entity/head_skeleton.png")
    };
    public static final ResourceLocation TEXTURE_HAY = new ResourceLocation("minecraft", "textures/block/hay_block_top.png");
    public static final ResourceLocation TEXTURE_EFFECT = new ResourceLocation(MOD_ID, "textures/entity/magicfield.png");
    public static final ResourceLocation BARS = new ResourceLocation("minecraft", "textures/gui/bars.png");
}
