package ovh.corail.flying_things.datagen;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		if(event.includeServer()) {
			generator.addProvider(new RecipeGenerator(generator));
		}
	}
	
}
