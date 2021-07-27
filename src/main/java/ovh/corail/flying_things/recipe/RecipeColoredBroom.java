package ovh.corail.flying_things.recipe;

import java.util.Arrays;
import java.util.stream.Stream;

import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ovh.corail.flying_things.ModFlyingThings;
import ovh.corail.flying_things.item.ItemEnchantedBroom;

public class RecipeColoredBroom extends ShapelessRecipe {

    private final int modelType;
    private final Item dye;

    public static NonNullList<Ingredient> getInputs(Item broom, Item dye, int filterModelType) {
    	Stream<ItemStack> inputBrooms = Arrays.stream(DyeColor.values())
    			.filter(d -> d.getId() != filterModelType)
    			.map(d -> ItemEnchantedBroom.setModelType(new ItemStack(broom), d.getId()));
    	Ingredient brooms = Ingredient.fromStacks(inputBrooms);
    	Ingredient dyeIng = Ingredient.fromItems(dye);
    	NonNullList<Ingredient> out = NonNullList.create();
    	out.add(brooms);
    	out.add(dyeIng);
    	return out;
    }
    
    public RecipeColoredBroom(ResourceLocation id, Item broom, Item dye, int modelType) {
		super(id, "", ItemEnchantedBroom.setModelType(new ItemStack(broom), modelType), getInputs(broom, dye, modelType));
		this.modelType = modelType;
		this.dye = dye;
	}
    
    /*
    @Override
	public boolean matches(CraftingInventory inv, World worldIn) {
    	boolean hasBroom = false, hasDye = false;
        int count = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i).isEmpty()) {
                continue;
            }
            if (inv.getStackInSlot(i).getItem() == this.broom) {
                hasBroom = true;
                count++;
                continue;
            } else if (inv.getStackInSlot(i).getItem() == this.dye) {
                hasDye = true;
                count++;
                continue;
            }
            if (count > 2) {
                return false;
            }
        }
        return count == 2 && hasBroom && hasDye;
	}
	*/

    /*
	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack broom = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() == this.broom) {
                broom = stack.copy();
            }
            if (stack.getItem() == this.dye) {
                dye = stack;
            }
        }
        if (broom.isEmpty() || dye.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return ItemEnchantedBroom.setModelType(broom, this.modelType);
	}
	*/

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(ModFlyingThings.MOD_ID, "colored_broom"));
	}
	
	public Item getDye() {
		return dye;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeColoredBroom> {

		@Override
		public RecipeColoredBroom read(ResourceLocation recipeId, JsonObject json) {
			Item broom = JSONUtils.getItem(json, "broom");
			Item dye = JSONUtils.getItem(json, "dye");
			int modelType = JSONUtils.getInt(json, "modelType");
			return new RecipeColoredBroom(recipeId, broom, dye, modelType);
		}

		@Override
		public RecipeColoredBroom read(ResourceLocation recipeId, PacketBuffer buffer) {
			ResourceLocation broomRL = buffer.readResourceLocation();
			ResourceLocation dyeRL = buffer.readResourceLocation();
			int modelType = buffer.readVarInt();
			
			Item broom = ForgeRegistries.ITEMS.getValue(broomRL);
			Item dye = ForgeRegistries.ITEMS.getValue(dyeRL);
			
			return new RecipeColoredBroom(recipeId, broom, dye, modelType);
		}

		@Override
		public void write(PacketBuffer buffer, RecipeColoredBroom recipe) {
			buffer.writeResourceLocation(recipe.getRecipeOutput().getItem().getRegistryName());
			buffer.writeResourceLocation(recipe.dye.getRegistryName());
			buffer.writeVarInt(recipe.modelType);
		}
    	
    }

}
