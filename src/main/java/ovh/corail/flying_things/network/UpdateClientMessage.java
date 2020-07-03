package ovh.corail.flying_things.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import ovh.corail.flying_things.ConfigFlyingThings;

import java.util.function.Supplier;

public class UpdateClientMessage {
    private int speedMax, accelerationMax, accelerationIncrement, speedMaxNoEnergy, maxEnergy;
    private boolean allowTombstoneSoulbound;

    public UpdateClientMessage(int speedMax, int accelerationMax, int accelerationIncrement, int speedMaxNoEnergy, int maxEnergy, boolean allowTombstoneSoulbound) {
        this.speedMax = speedMax;
        this.accelerationMax = accelerationMax;
        this.accelerationIncrement = accelerationIncrement;
        this.speedMaxNoEnergy = speedMaxNoEnergy;
        this.maxEnergy = maxEnergy;
        this.allowTombstoneSoulbound = allowTombstoneSoulbound;
    }

    public static UpdateClientMessage fromBytes(PacketBuffer buf) {
        return new UpdateClientMessage(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean());
    }

    public static void toBytes(UpdateClientMessage msg, PacketBuffer buf) {
        buf.writeInt(msg.speedMax);
        buf.writeInt(msg.accelerationMax);
        buf.writeInt(msg.accelerationIncrement);
        buf.writeInt(msg.speedMaxNoEnergy);
        buf.writeInt(msg.maxEnergy);
        buf.writeBoolean(msg.allowTombstoneSoulbound);
    }

    public static class Handler {
        public static void handle(final UpdateClientMessage message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ConfigFlyingThings.General.serverSpeedMax = message.speedMax;
                ConfigFlyingThings.General.serverAccelerationMax = message.accelerationMax;
                ConfigFlyingThings.General.serverAccelerationIncrement = message.accelerationIncrement;
                ConfigFlyingThings.General.serverSpeedMaxNoEnergy = message.speedMaxNoEnergy;
                ConfigFlyingThings.General.serverMaxEnergy = message.maxEnergy;
                ConfigFlyingThings.General.serverAllowTombstoneSoulbound = message.allowTombstoneSoulbound;
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
