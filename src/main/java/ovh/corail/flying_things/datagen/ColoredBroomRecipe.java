package ovh.corail.flying_things.datagen;

import java.util.function.Consumer;

import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import ovh.corail.flying_things.ModFlyingThings;

public class ColoredBroomRecipe implements IFinishedRecipe {
	
	private final ResourceLocation id;
    private final Item broom;
	private final DyeItem dye;
	private final int modelType;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation advancementId;

	public ColoredBroomRecipe(ResourceLocation id, Item broom, DyeItem dye, int modelType, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
		this.id = id;
		this.broom = broom;
		this.dye = dye;
		this.modelType = modelType;
		this.advancementBuilder = advancementBuilder;
		this.advancementId = advancementId;
	}
	
	@Override
	public void serializeRecipeData(JsonObject json) {
		json.addProperty("broom", broom.getRegistryName().toString());
		json.addProperty("dye", dye.getRegistryName().toString());
		json.addProperty("modelType", this.modelType);
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public IRecipeSerializer<?> getType() {
		return ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(ModFlyingThings.MOD_ID, "colored_broom"));
	}

	@Override
	public JsonObject serializeAdvancement() {
		return this.advancementBuilder.serializeToJson();
	}

	@Override
	public ResourceLocation getAdvancementId() {
		return this.advancementId;
	}
	
	public static Builder recipe(IItemProvider result) {
		return new Builder(result);
	}

	public static class Builder {

		private final Item broom;
		private DyeItem dye;
		private int modelType;
		private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

		public Builder(IItemProvider broom) {
			this.broom = broom.asItem();
		}

		public Builder dye(IItemProvider dye) {
			if(dye.asItem() instanceof DyeItem) {
				this.dye = (DyeItem) dye.asItem();
				this.modelType = this.dye.getDyeColor().getId();
				return this;
			}
			throw new IllegalArgumentException("provided dye item ist not a DyeItem");
		}

		public Builder modelType(int id) {
			this.modelType = id;
			return this;
		}

		public Builder addCriterion(String name, ICriterionInstance criterionIn) {
			this.advancementBuilder.addCriterion(name, criterionIn);
			return this;
		}
		
		public void build(Consumer<IFinishedRecipe> consumerIn) {
			this.build(consumerIn, this.broom.getRegistryName());
		}
		
		public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
			this.advancementBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(IRequirementsStrategy.OR);
			ResourceLocation advancmentRL = new ResourceLocation(id.getNamespace(), "recipes/" + this.broom.getItemCategory().getRecipeFolderName() + "/" + id.getPath());
			consumerIn.accept(new ColoredBroomRecipe(id, this.broom, this.dye, this.modelType, this.advancementBuilder, advancmentRL));
		}

	}
}
