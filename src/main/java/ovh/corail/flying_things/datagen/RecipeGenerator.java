package ovh.corail.flying_things.datagen;

import java.util.Arrays;
import java.util.function.Consumer;

import com.google.gson.JsonObject;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import ovh.corail.flying_things.ModFlyingThings;
import ovh.corail.flying_things.registry.ModItems;

public class RecipeGenerator extends RecipeProvider {

	public RecipeGenerator(DataGenerator generatorIn) {
		super(generatorIn);
	}
	
	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(ModItems.enchantedBroom)
			.patternLine("012")
			.patternLine("041")
			.patternLine("300")
			.key('0', Items.LAPIS_LAZULI)
			.key('1', Items.STRING)
			.key('2', Items.WHEAT)
			.key('3', ItemTags.LOGS)
			.key('4', ModItems.phialOfAnimation)
			.addCriterion("has_phial", hasItem(ModItems.phialOfAnimation))
			.build(getNBTConsumer(consumer, "{model_type:12}"));
		
		Arrays.stream(DyeColor.values())
			.map(dye -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(dye.getTranslationKey() + "_dye")))
			.forEach(dye -> {
				String color = ((DyeItem)dye).getDyeColor().getTranslationKey();
				ColoredBroomRecipe.recipe(ModItems.enchantedBroom)
					.dye(dye)
					.addCriterion("has_" + color, hasItem(dye))
					.build(consumer, new ResourceLocation(ModFlyingThings.MOD_ID, "broom_" + color));
			});
	}
	
	private Consumer<IFinishedRecipe> getNBTConsumer(Consumer<IFinishedRecipe> delegate, String nbt) {
		return recipe -> delegate.accept(new NbtRecipe(recipe, nbt));
	}
	
	private static class NbtRecipe implements IFinishedRecipe {
		
		private final IFinishedRecipe delegate;
		private final String nbtValue;
		
		public NbtRecipe(IFinishedRecipe delegate, String nbtValue) {
			this.delegate = delegate;
			this.nbtValue = nbtValue;
		}

		@Override
		public void serialize(JsonObject json) {
			delegate.serialize(json);
			json.getAsJsonObject("result").addProperty("nbt", nbtValue);
		}

		@Override
		public ResourceLocation getID() {
			return delegate.getID();
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return delegate.getSerializer();
		}

		@Override
		public JsonObject getAdvancementJson() {
			return delegate.getAdvancementJson();
		}

		@Override
		public ResourceLocation getAdvancementID() {
			return delegate.getAdvancementID();
		}
		
	}

}
