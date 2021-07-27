package ovh.corail.flying_things.event;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
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
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.gui.GuiConfig;
import ovh.corail.flying_things.gui.GuiOverlayEnergy;
import ovh.corail.flying_things.helper.Helper;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;
import static ovh.corail.flying_things.ModFlyingThings.MOD_NAME;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@SuppressWarnings({ "unused", "FieldCanBeLocal" })
public class ClientEventHandler {
    private static KeyBinding keybindConfig;
    
    public static void registerKeybind() {
    	ClientRegistry.registerKeyBinding(keybindConfig = new KeyBinding("Configuration Screen", KeyConflictContext.IN_GAME, InputMappings.INPUT_INVALID, MOD_NAME));
    }

    private static boolean HAS_TRUE_SIGHT = false, REQUIRE_REMOVAL_NIGHTVISION = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onFogDensity(FogDensity event) {
        if (HAS_TRUE_SIGHT && event.getInfo().getBlockAtCamera().getMaterial() == Material.WATER) {
            event.setCanceled(true);
            event.setDensity(event.getDensity() / 4f);
            RenderSystem.fogMode(GlStateManager.FogMode.EXP);
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
            Helper.removeClientPotionEffect(Effects.NIGHT_VISION);
            REQUIRE_REMOVAL_NIGHTVISION = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGuiRender(DrawScreenEvent event) {
        if (REQUIRE_REMOVAL_NIGHTVISION) {
            Helper.removeClientPotionEffect(Effects.NIGHT_VISION);
            REQUIRE_REMOVAL_NIGHTVISION = false;
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGameOverlayEvent.Post event) {
        if (event.getType() == ElementType.EXPERIENCE) {
            Minecraft mc = Minecraft.getInstance();
            if (Helper.isRidingFlyingThing(mc.player)) {
                new GuiOverlayEnergy(mc);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.world == null || mc.isGamePaused()) {
            return;
        }
        if (event.phase == TickEvent.Phase.END && Helper.isValidPlayer(mc.player)) {
            // open gui knowledge
            if (keybindConfig.isPressed()) {
                if (mc.currentScreen == null || mc.currentScreen instanceof ChatScreen) {
                    mc.displayGuiScreen(new GuiConfig(mc));
                }
            }
        }
    }
}
