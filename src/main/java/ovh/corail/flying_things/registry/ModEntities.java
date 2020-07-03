package ovh.corail.flying_things.registry;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import ovh.corail.flying_things.entity.EntityEnchantedBroom;
import ovh.corail.flying_things.entity.EntityMagicCarpet;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

@ObjectHolder(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities extends Registrable {
    public static final EntityType<EntityMagicCarpet> magic_carpet;
    public static final EntityType<EntityEnchantedBroom> enchanted_broom;

    static {
        magic_carpet = EntityType.Builder.<EntityMagicCarpet>create(EntityMagicCarpet::new, EntityClassification.MISC).setTrackingRange(80).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).size(1.5f, 0.3f).setCustomClientFactory(EntityMagicCarpet::new).disableSummoning().build(MOD_ID + ":magic_carpet");
        enchanted_broom = EntityType.Builder.<EntityEnchantedBroom>create(EntityEnchantedBroom::new, EntityClassification.MISC).setTrackingRange(80).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).size(1.2f, 0.2f).setCustomClientFactory(EntityEnchantedBroom::new).disableSummoning().build(MOD_ID + ":enchanted_broom");
    }

    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
        registerForgeEntry(event.getRegistry(), magic_carpet, "magic_carpet");
        registerForgeEntry(event.getRegistry(), enchanted_broom, "enchanted_broom");
    }
}
