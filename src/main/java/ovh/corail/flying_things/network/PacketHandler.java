package ovh.corail.flying_things.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;
import static ovh.corail.flying_things.ModFlyingThings.MOD_VER;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = MOD_ID + "-" + MOD_VER;
    private static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "flying_things_channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    static {
        HANDLER.registerMessage(0, UpdateConfigMessage.class, UpdateConfigMessage::toBytes, UpdateConfigMessage::fromBytes, UpdateConfigMessage.Handler::handle);
    }

    public static <T> void sendToAllPlayers(T message) {
        HANDLER.send(PacketDistributor.ALL.noArg(), message);
    }
}
