package ovh.corail.flying_things.registry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ovh.corail.flying_things.recipe.RecipeColoredBroom;
import ovh.corail.flying_things.recipe.RecipePumkinBroom;
import ovh.corail.flying_things.recipe.RecipeRaidCarpet;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSerializers {

    @SubscribeEvent
    public static void onRegisterSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeColoredBroom::new).setRegistryName(MOD_ID, "colored_broom"));
        event.getRegistry().register(new SpecialRecipeSerializer<>(RecipePumkinBroom::new).setRegistryName(MOD_ID, "pumpkin_broom"));
        event.getRegistry().register(new SpecialRecipeSerializer<>(rl -> new RecipeRaidCarpet(rl, 18, Ingredient.fromStacks(new ItemStack(Items.PRISMARINE_CRYSTALS)))).setRegistryName(MOD_ID, "pillage_0"));
        event.getRegistry().register(new SpecialRecipeSerializer<>(rl -> new RecipeRaidCarpet(rl, 19, Ingredient.fromStacks(new ItemStack(Items.QUARTZ)))).setRegistryName(MOD_ID, "pillage_1"));
    }
}
