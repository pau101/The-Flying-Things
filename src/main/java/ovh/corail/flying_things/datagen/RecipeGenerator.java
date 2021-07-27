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
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(ModItems.enchantedBroom.get())
			.pattern("012")
			.pattern("041")
			.pattern("300")
			.define('0', Items.LAPIS_LAZULI)
			.define('1', Items.STRING)
			.define('2', Items.WHEAT)
			.define('3', ItemTags.LOGS)
			.define('4', ModItems.phialOfAnimation.get())
			.unlockedBy("has_phial", has(ModItems.phialOfAnimation.get()))
			.save(getNBTConsumer(consumer, "{model_type:12}"));
		
		Arrays.stream(DyeColor.values())
			.map(dye -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(dye.getName() + "_dye")))
			.forEach(dye -> {
				String color = ((DyeItem)dye).getDyeColor().getName();
				ColoredBroomRecipe.recipe(ModItems.enchantedBroom.get())
					.dye(dye)
					.addCriterion("has_" + color, has(dye))
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
		public void serializeRecipeData(JsonObject json) {
			delegate.serializeRecipeData(json);
			json.getAsJsonObject("result").addProperty("nbt", nbtValue);
		}

		@Override
		public ResourceLocation getId() {
			return delegate.getId();
		}

		@Override
		public IRecipeSerializer<?> getType() {
			return delegate.getType();
		}

		@Override
		public JsonObject serializeAdvancement() {
			return delegate.serializeAdvancement();
		}

		@Override
		public ResourceLocation getAdvancementId() {
			return delegate.getAdvancementId();
		}
		
	}

}
