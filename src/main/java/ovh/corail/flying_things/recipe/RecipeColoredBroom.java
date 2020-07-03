package ovh.corail.flying_things.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import ovh.corail.flying_things.item.ItemEnchantedBroom;
import ovh.corail.flying_things.registry.ModItems;

public class RecipeColoredBroom extends ShapelessRecipe {
    private static final NonNullList<Ingredient> INGREDIENTS = NonNullList.create();

    static {
        INGREDIENTS.add(Ingredient.fromStacks(new ItemStack(ModItems.enchantedBroom)));
        INGREDIENTS.add(Ingredient.fromTag(Tags.Items.DYES));
    }

    public RecipeColoredBroom(ResourceLocation rl) {
        super(rl, "", new ItemStack(ModItems.enchantedBroom), INGREDIENTS);
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        boolean hasBroom = false, hasDye = false;
        int count = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i).isEmpty()) {
                continue;
            }
            if (inv.getStackInSlot(i).getItem() == ModItems.enchantedBroom) {
                hasBroom = true;
                count++;
                continue;
            } else if (INGREDIENTS.get(1).test(inv.getStackInSlot(i))) {
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

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack broom = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() == ModItems.enchantedBroom) {
                broom = stack.copy();
            }
            if (INGREDIENTS.get(1).test(stack)) {
                dye = stack;
            }
        }
        if (broom.isEmpty() || dye.isEmpty() || !(dye.getItem() instanceof DyeItem)) {
            return ItemStack.EMPTY;
        }
        return ItemEnchantedBroom.setModelType(broom, ((DyeItem) dye.getItem()).getDyeColor().getId());
    }
}
