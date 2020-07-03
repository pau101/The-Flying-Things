package ovh.corail.flying_things.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.item.ItemEnchantedBroom;
import ovh.corail.flying_things.registry.ModItems;

public class RecipePumkinBroom extends ShapelessRecipe {
    private static final NonNullList<Ingredient> INGREDIENTS = NonNullList.create();

    static {
        INGREDIENTS.add(Ingredient.fromStacks(ItemEnchantedBroom.setModelType(new ItemStack(ModItems.enchantedBroom), 12)));
        INGREDIENTS.add(Ingredient.fromStacks(new ItemStack(Blocks.PUMPKIN)));
    }

    public RecipePumkinBroom(ResourceLocation rl) {
        super(rl, "", ItemEnchantedBroom.setHeadType(new ItemStack(ModItems.enchantedBroom), 1), INGREDIENTS);
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        if (world == null || (!Helper.isDateAroundHalloween() && !ConfigFlyingThings.general.persistantHolidays.get())) {
            return false;
        }
        boolean hasBroom = false, hasPumkin = false;
        int count = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i).isEmpty()) {
                continue;
            }
            if (inv.getStackInSlot(i).getItem() == ModItems.enchantedBroom) {
                hasBroom = true;
                count++;
            } else if (INGREDIENTS.get(1).test(inv.getStackInSlot(i))) {
                hasPumkin = true;
                count++;
            }
            if (count > 2) {
                return false;
            }
        }
        return count == 2 && hasBroom && hasPumkin;
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
        return broom.isEmpty() || dye.isEmpty() ? ItemStack.EMPTY : ItemEnchantedBroom.setHeadType(broom, 1);
    }
}
