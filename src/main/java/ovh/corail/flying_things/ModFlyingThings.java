package ovh.corail.flying_things;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ovh.corail.flying_things.entity.EntityEnchantedBroom;
import ovh.corail.flying_things.entity.EntityMagicCarpet;
import ovh.corail.flying_things.network.UpdateClientMessage;
import ovh.corail.flying_things.render.RenderEnchantedBroom;
import ovh.corail.flying_things.render.RenderMagicCarpet;

@Mod("flying_things")
public class ModFlyingThings {
    public static final String MOD_ID = "flying_things";
    public static final String MOD_NAME = "The Flying Things";
    public static final Logger LOGGER = LogManager.getLogger(ModFlyingThings.class);
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "flying_things_channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public ModFlyingThings() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigFlyingThings.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigFlyingThings.GENERAL_SPEC);
        HANDLER.registerMessage(0, UpdateClientMessage.class, UpdateClientMessage::toBytes, UpdateClientMessage::fromBytes, UpdateClientMessage.Handler::handle);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityEnchantedBroom.class, RenderEnchantedBroom::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMagicCarpet.class, RenderMagicCarpet::new);
    }
}
