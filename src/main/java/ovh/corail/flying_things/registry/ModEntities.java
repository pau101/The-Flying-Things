package ovh.corail.flying_things.registry;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ovh.corail.flying_things.entity.EntityEnchantedBroom;
import ovh.corail.flying_things.entity.EntityMagicCarpet;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
	
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);
	
	public static final RegistryObject<EntityType<EntityMagicCarpet>> magic_carpet = ENTITIES.register("magic_carpet", () -> {
		return EntityType.Builder.<EntityMagicCarpet>create(EntityMagicCarpet::new, EntityClassification.MISC)
			.setTrackingRange(80)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.size(1.5f, 0.3f)
			.setCustomClientFactory(EntityMagicCarpet::new)
			.disableSummoning()
			.build(MOD_ID + ":magic_carpet");
	});
	
	public static final RegistryObject<EntityType<EntityEnchantedBroom>> enchanted_broom = ENTITIES.register("enchanted_broom", () -> {
		return EntityType.Builder.<EntityEnchantedBroom>create(EntityEnchantedBroom::new, EntityClassification.MISC)
				.setTrackingRange(80)
				.setUpdateInterval(1)
				.setShouldReceiveVelocityUpdates(true)
				.size(1.2f, 0.2f)
				.setCustomClientFactory(EntityEnchantedBroom::new)
				.disableSummoning()
				.build(MOD_ID + ":enchanted_broom");
	});
	
}
