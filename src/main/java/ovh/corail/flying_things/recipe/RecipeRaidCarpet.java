package ovh.corail.flying_things.recipe;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.potion.Effects;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import ovh.corail.flying_things.item.ItemMagicCarpet;
import ovh.corail.flying_things.registry.ModItems;

import javax.annotation.Nullable;

public class RecipeRaidCarpet extends ShapedRecipe {
    private static final Ingredient BROWN_CARPET = Ingredient.of(new ItemStack(Items.BROWN_CARPET));
    private static final Ingredient SHIELD = Ingredient.of(new ItemStack(Items.SHIELD));

    public RecipeRaidCarpet(ResourceLocation rl, int modelType, Ingredient gemIngredient) {
        super(rl, "", 3, 3, NonNullList.of(Ingredient.EMPTY,
                BROWN_CARPET, gemIngredient, BROWN_CARPET,
                BROWN_CARPET, SHIELD, BROWN_CARPET,
                BROWN_CARPET, gemIngredient, BROWN_CARPET
        ), ItemMagicCarpet.setModelType(new ItemStack(ModItems.magicCarpet.get()), modelType));
    }

    @Override
    public ItemStack assemble(CraftingInventory inv) {
        PlayerEntity player = getPlayer(inv);
        if (player != null && player.hasEffect(Effects.HERO_OF_THE_VILLAGE)) {
            return getResultItem().copy();
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    private static PlayerEntity getPlayer(final CraftingInventory inventory) {
        return inventory.menu.slots.stream()
                .map(slot -> slot.container)
                .filter(PlayerInventory.class::isInstance)
                .map(PlayerInventory.class::cast)
                .map(inv -> inv.player)
                .findFirst().orElse(null);
    }
}
