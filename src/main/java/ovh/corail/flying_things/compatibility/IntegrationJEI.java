package ovh.corail.flying_things.compatibility;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.ModFlyingThings;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.item.ItemAbstractFlyingThing;
import ovh.corail.flying_things.item.ItemEnchantedBroom;
import ovh.corail.flying_things.recipe.RecipeColoredBroom;
import ovh.corail.flying_things.registry.ModItems;

import javax.annotation.Nonnull;

@JeiPlugin
public class IntegrationJEI implements IModPlugin {
    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ModFlyingThings.MOD_ID);
    }
    
    private static class ColoredBroomExtension implements ICraftingCategoryExtension {
    	
    	private final ResourceLocation id;
    	private final ItemStack broomOut;
    	private final Item dye;
    
		public ColoredBroomExtension(ResourceLocation id, ItemStack broomOut, Item dye) {
			this.id = id;
			this.broomOut = broomOut;
			this.dye = dye;
		}

		@Override
		public void setIngredients(IIngredients ingredients) {
			ingredients.setInputIngredients(RecipeColoredBroom.getInputs(this.broomOut.getItem(), this.dye, ItemEnchantedBroom.getModelType(broomOut)));
			ingredients.setOutput(VanillaTypes.ITEM, this.broomOut);
		}
		
		@Override
		public ResourceLocation getRegistryName() {
			return this.id;
		}
    	
    }
    
    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
    	registration.getCraftingCategory().addCategoryExtension(RecipeColoredBroom.class, recipe -> {
    		return new ColoredBroomExtension(recipe.getId(), recipe.getRecipeOutput(), recipe.getDye());
    	});
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();

        if (Helper.isDateAroundHalloween() || ConfigFlyingThings.general.persistantHolidays.get()) {
            for (DyeColor dye : DyeColor.values()) {
                ItemStack currentStack = ItemEnchantedBroom.setModelType(new ItemStack(ModItems.enchantedBroom), dye.getId());
                builder.add(new ShapelessRecipe(new ResourceLocation(ModFlyingThings.MOD_ID + ":pumpkin_broom"), "pumpkin_broom",
                        ItemEnchantedBroom.setHeadType(currentStack.copy(), 1), NonNullList.from(Ingredient.EMPTY,
                        Ingredient.fromStacks(new ItemStack(Blocks.PUMPKIN)),
                        Ingredient.fromStacks(currentStack)
                )));
            }
        }
        registry.addRecipes(builder.build(), VanillaRecipeCategoryUid.CRAFTING);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
    	registration.registerSubtypeInterpreter(ModItems.enchantedBroom, (stack, context) -> String.valueOf(ItemAbstractFlyingThing.getModelType(stack)) + ItemEnchantedBroom.getHeadType(stack));
        registration.registerSubtypeInterpreter(ModItems.magicCarpet, (stack, context) -> String.valueOf(ItemAbstractFlyingThing.getModelType(stack)));
    }
}
