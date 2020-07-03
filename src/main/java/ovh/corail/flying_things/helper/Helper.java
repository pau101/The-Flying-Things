package ovh.corail.flying_things.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;
import org.lwjgl.opengl.GL11;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.registry.ModEntities;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Random;

public class Helper {
    @Nullable
    private static Boolean isHalloween = null;

    public static final Random random = new Random();

    public static int getRandom(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static boolean isValidPlayer(@Nullable Entity entity) {
        return entity instanceof PlayerEntity;
    }

    public static boolean isValidPlayerMP(@Nullable Entity entity) {
        return isValidPlayer(entity) && !entity.world.isRemote;
    }

    public static boolean isFlyingthing(@Nullable Entity entity) {
        return entity != null && (entity.getType() == ModEntities.enchanted_broom || entity.getType() == ModEntities.magic_carpet);
    }

    public static boolean isBoss(@Nullable Entity entity) {
        return entity != null && !entity.isNonBoss();
    }

    public static boolean isRidingFlyingThing(@Nullable Entity entity) {
        return entity != null && isFlyingthing(entity.getRidingEntity());
    }

    public static boolean isControllingFlyingThing(@Nullable Entity entity) {
        return isRidingFlyingThing(entity) && entity.getRidingEntity().getControllingPassenger() == entity;
    }

    public static String getDimensionString(DimensionType dimensionType) {
        ResourceLocation rl = DimensionType.getKey(dimensionType);
        return rl == null ? "" : rl.toString();
    }

    public static boolean isDateAroundHalloween() {
        if (ConfigFlyingThings.general.persistantHolidays.get()) {
            return true;
        }
        if (isHalloween == null) {
            LocalDate date = LocalDate.now();
            isHalloween = date.get(ChronoField.MONTH_OF_YEAR) + 1 == 10 && date.get(ChronoField.DAY_OF_MONTH) >= 20 || date.get(ChronoField.MONTH_OF_YEAR) + 1 == 11 && date.get(ChronoField.DAY_OF_MONTH) <= 3;
        }
        return isHalloween;
    }

    @OnlyIn(Dist.CLIENT)
    public static String getNameForKeybindSneak() {
        return Minecraft.getInstance().gameSettings.keyBindSneak.getLocalizedName();
    }

    @OnlyIn(Dist.CLIENT)
    public static void removeClientPotionEffect(Effect effect) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            player.removeActivePotionEffect(effect);
        }
    }

    @Nullable
    public static MinecraftServer getServer() {
        try {
            MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
            if (server != null && server.isOnExecutionThread()) {
                return server;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static boolean isPacketToClient(NetworkEvent.Context ctx) {
        return ctx.getDirection().getOriginationSide() == LogicalSide.SERVER && ctx.getDirection().getReceptionSide() == LogicalSide.CLIENT;
    }

    public static boolean isPacketToServer(NetworkEvent.Context ctx) {
        return ctx.getDirection().getOriginationSide() == LogicalSide.CLIENT && ctx.getDirection().getReceptionSide() == LogicalSide.SERVER;
    }

    public static float[] getRGBColor4F(int color) {
        float[] rgb = new float[4];
        rgb[0] = (float) (color >> 16 & 255) / 255f;
        rgb[1] = (float) (color >> 8 & 255) / 255f;
        rgb[2] = (float) (color & 255) / 255f;
        rgb[3] = (float) (color >> 24 & 255) / 255f;
        return rgb;
    }

    public static float[] getRGBColor3F(int color) {
        float[] rgb = new float[3];
        rgb[0] = (color >> 16 & 255) / 255f;
        rgb[1] = (color >> 8 & 255) / 255f;
        rgb[2] = (color & 255) / 255f;
        return rgb;
    }

    public static int[] getRGBColor3I(int color) {
        int[] rgb = new int[3];
        rgb[0] = color >> 16 & 255;
        rgb[1] = color >> 8 & 255;
        rgb[2] = color & 255;
        return rgb;
    }

    public static void fillGradient(int left, int top, int right, int bottom, int color1, int color2, int zLevel, boolean isHorizontal) {
        float[] argb1 = Helper.getRGBColor4F(color1);
        float[] argb2 = Helper.getRGBColor4F(color2);

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        makeVertex(bufferbuilder, right, top, zLevel, isHorizontal ? argb2 : argb1);
        makeVertex(bufferbuilder, left, top, zLevel, argb1);
        makeVertex(bufferbuilder, left, bottom, zLevel, isHorizontal ? argb1 : argb2);
        makeVertex(bufferbuilder, right, bottom, zLevel, argb2);
        tessellator.draw();
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    private static void makeVertex(BufferBuilder bufferbuilder, int x, int y, int zLevel, float[] colorArray) {
        bufferbuilder.pos(x, y, zLevel).color(colorArray[0], colorArray[1], colorArray[2], colorArray[3]).endVertex();
    }
}
