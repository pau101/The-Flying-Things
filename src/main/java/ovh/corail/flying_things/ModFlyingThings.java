package ovh.corail.flying_things;

import com.google.common.reflect.Reflection;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.config.FlyingThingsModConfig;
import ovh.corail.flying_things.gui.GuiConfig;
import ovh.corail.flying_things.network.PacketHandler;
import ovh.corail.flying_things.proxy.ClientProxy;
import ovh.corail.flying_things.proxy.IProxy;
import ovh.corail.flying_things.proxy.ServerProxy;
import ovh.corail.flying_things.registry.ModEntities;
import ovh.corail.flying_things.render.RenderEnchantedBroom;
import ovh.corail.flying_things.render.RenderMagicCarpet;

@Mod("flying_things")
public class ModFlyingThings {
    public static final String MOD_ID = "flying_things";
    public static final String MOD_NAME = "The Flying Things";
    public static final String MOD_VER = "1.8.9";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final IProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    @SuppressWarnings("UnstableApiUsage")
    public ModFlyingThings() {
        Reflection.initialize(PacketHandler.class);
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.CLIENT, ConfigFlyingThings.CLIENT_SPEC);
        context.registerConfig(ModConfig.Type.COMMON, ConfigFlyingThings.GENERAL_SPEC);
        PROXY.preInit();
        registerSharedConfig(context);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        context.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> GuiConfig::new);
    }

    private void registerSharedConfig(ModLoadingContext context) {
        context.getActiveContainer().addConfig(new FlyingThingsModConfig(ConfigFlyingThings.SHARED_SPEC, context.getActiveContainer()));
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.enchanted_broom, RenderEnchantedBroom::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.magic_carpet, RenderMagicCarpet::new);
    }
}
