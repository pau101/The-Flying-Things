package ovh.corail.flying_things.event;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ovh.corail.flying_things.ConfigFlyingThings;
import ovh.corail.flying_things.gui.GuiOverlayEnergy;
import ovh.corail.flying_things.gui.GuiOverlaySpeed;
import ovh.corail.flying_things.helper.Helper;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@SuppressWarnings({ "unused", "FieldCanBeLocal" })
public class ClientEventHandler {
    private static boolean IS_DEV = false;
    private static boolean HAS_TRUE_SIGHT = false, REQUIRE_REMOVAL_NIGHTVISION = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onFogDensity(FogDensity event) {
        if (HAS_TRUE_SIGHT && event.getInfo().getBlockAtCamera().getMaterial() == Material.WATER) {
            event.setCanceled(true);
            event.setDensity(event.getDensity() / 4f);
            GlStateManager.fogMode(GlStateManager.FogMode.EXP);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onRenderFog(FogColors event) {
        if (HAS_TRUE_SIGHT && event.getInfo().getBlockAtCamera().getMaterial() == Material.WATER) {
            event.setRed(23f / 255f);
            event.setGreen(106f / 255f);
            event.setBlue(236f / 255f);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderBlockLayer(RenderBlockOverlayEvent event) {
        if (HAS_TRUE_SIGHT && event.getOverlayType() == OverlayType.WATER) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            HAS_TRUE_SIGHT = ConfigFlyingThings.general.allowToFlyInWater.get() && Helper.isRidingFlyingThing(player) && Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getBlockAtCamera().getMaterial() == Material.WATER;
            if (HAS_TRUE_SIGHT) {
                if (!player.isPotionActive(Effects.NIGHT_VISION)) {
                    player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 1200, 0, true, false));
                    REQUIRE_REMOVAL_NIGHTVISION = true;
                }
            }
        } else if (REQUIRE_REMOVAL_NIGHTVISION) {
            Minecraft.getInstance().player.removeActivePotionEffect(Effects.NIGHT_VISION);
            REQUIRE_REMOVAL_NIGHTVISION = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGuiRender(DrawScreenEvent event) {
        if (REQUIRE_REMOVAL_NIGHTVISION) {
            Minecraft.getInstance().player.removeActivePotionEffect(Effects.NIGHT_VISION);
            REQUIRE_REMOVAL_NIGHTVISION = false;
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGameOverlayEvent.Post event) {
        if (event.getType() != ElementType.EXPERIENCE) {
            return;
        }
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (Helper.isRidingFlyingThing(player)) {
            new GuiOverlayEnergy(Minecraft.getInstance());
            if (IS_DEV && Minecraft.getInstance().gameSettings.showDebugInfo) {
                new GuiOverlaySpeed(Minecraft.getInstance());
            }
        }
    }
}
