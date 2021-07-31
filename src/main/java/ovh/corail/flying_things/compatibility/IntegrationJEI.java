package ovh.corail.flying_things.compatibility;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import ovh.corail.flying_things.ModFlyingThings;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.item.ItemAbstractFlyingThing;
import ovh.corail.flying_things.item.ItemEnchantedBroom;
import ovh.corail.flying_things.registry.ModItems;

import javax.annotation.Nonnull;

@JeiPlugin
public class IntegrationJEI implements IModPlugin {
    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ModFlyingThings.MOD_ID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();

        if (Helper.isDateAroundHalloween()) {
            for (DyeColor dye : DyeColor.values()) {
                ItemStack currentStack = ItemEnchantedBroom.setModelType(new ItemStack(ModItems.enchantedBroom.get()), dye.getId());
                builder.add(new ShapelessRecipe(new ResourceLocation(ModFlyingThings.MOD_ID + ":pumpkin_broom" + dye.getId()), "pumpkin_broom",
                        ItemEnchantedBroom.setHeadType(currentStack.copy(), 1), NonNullList.of(Ingredient.EMPTY,
                        Ingredient.of(new ItemStack(Blocks.PUMPKIN)),
                        Ingredient.of(currentStack)
                )));
            }
        }
        registry.addRecipes(builder.build(), VanillaRecipeCategoryUid.CRAFTING);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
    	registration.registerSubtypeInterpreter(ModItems.enchantedBroom.get(), (stack, context) -> String.valueOf(ItemAbstractFlyingThing.getModelType(stack)) + ItemEnchantedBroom.getHeadType(stack));
        registration.registerSubtypeInterpreter(ModItems.magicCarpet.get(), (stack, context) -> String.valueOf(ItemAbstractFlyingThing.getModelType(stack)));
    }
}
