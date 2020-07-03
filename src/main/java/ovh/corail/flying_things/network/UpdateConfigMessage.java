package ovh.corail.flying_things.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.helper.Helper;

import java.util.function.Supplier;

public class UpdateConfigMessage {
    private boolean allowTombstoneSoulbound;
    private int[] intConfigs;

    public UpdateConfigMessage(boolean allowTombstoneSoulbound, int[] intConfigs) {
        this.allowTombstoneSoulbound = allowTombstoneSoulbound;
        this.intConfigs = intConfigs;
    }

    static UpdateConfigMessage fromBytes(PacketBuffer buf) {
        return new UpdateConfigMessage(buf.readBoolean(), buf.readVarIntArray());
    }

    static void toBytes(UpdateConfigMessage msg, PacketBuffer buf) {
        buf.writeBoolean(msg.allowTombstoneSoulbound);
        buf.writeVarIntArray(msg.intConfigs);
    }

    public static class Handler {
        static void handle(final UpdateConfigMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context ctx = contextSupplier.get();
            if (Helper.isPacketToClient(ctx)) {
                ctx.enqueueWork(() -> ConfigFlyingThings.updateConfig(message.allowTombstoneSoulbound, message.intConfigs));
            }
            ctx.setPacketHandled(true);
        }
    }
}
