package ovh.corail.flying_things.registry;

import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.item.ItemEnchantedBroom;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;
import static ovh.corail.flying_things.ModFlyingThings.MOD_NAME;

public class ModTabs {
    public static final ItemGroup tabFlyingThings = (new ItemGroup(MOD_ID) {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            ItemStack stack = new ItemStack(ModItems.enchantedBroom);
            ItemEnchantedBroom.setModelType(stack, Helper.getRandom(0, DyeColor.values().length - 1));
            return stack;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public String getTranslationKey() {
            return MOD_NAME;
        }
    });
}
