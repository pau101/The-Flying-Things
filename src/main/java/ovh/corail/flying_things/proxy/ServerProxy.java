package ovh.corail.flying_things.proxy;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ovh.corail.flying_things.ModFlyingThings;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.network.PacketHandler;

public class ServerProxy implements IProxy {
    private boolean isConfigDirty = false;

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void markConfigDirty() {
        this.isConfigDirty = true;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (this.isConfigDirty && event.phase == TickEvent.Phase.END) {
            this.isConfigDirty = false;
            MinecraftServer server = Helper.getServer();
            if (server != null && !server.getPlayerList().getPlayers().isEmpty()) {
                ModFlyingThings.LOGGER.info("Syncing Config on Client");
                PacketHandler.sendToAllPlayers(ConfigFlyingThings.getUpdatePacket());
            }
        }
    }
}
