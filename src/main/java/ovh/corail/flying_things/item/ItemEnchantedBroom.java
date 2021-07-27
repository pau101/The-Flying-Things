package ovh.corail.flying_things.item;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.entity.EntityAbstractFlyingThing;
import ovh.corail.flying_things.entity.EntityEnchantedBroom;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.helper.NBTStackHelper;
import ovh.corail.flying_things.registry.ModEntities;
import ovh.corail.flying_things.render.TEISREnchantedBroom;

import javax.annotation.Nullable;
import java.util.List;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

public class ItemEnchantedBroom extends ItemAbstractFlyingThing {

    public ItemEnchantedBroom() {
        super("enchanted_broom", getBuilder(true).maxStackSize(1).setISTER(() -> TEISREnchantedBroom::new));
    }

    @Override
    EntityType<?> getEntityType() {
        return ModEntities.enchanted_broom.get();
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            for (int i = 0; i < DyeColor.values().length; i++) {
                ItemStack stack = new ItemStack(this);
                setModelType(stack, DyeColor.values()[i].getId());
                items.add(stack);
            }
        }
    }

    @Override
    public boolean canFlyInDimension(RegistryKey<World> dimensionType) {
        return !ConfigFlyingThings.deniedDimensionToFly.deniedDimensionBroom.get().contains(Helper.getDimensionString(dimensionType));
    }

    @Override
    public int getActualRegen(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        boolean hasSpecialRegen = ConfigFlyingThings.general.allowSpecialRegen.get() && world.isBlockLoaded(entity.getPosition().down()) && world.getBlockState(entity.getPosition().down()).getBlock() == Blocks.RED_MUSHROOM_BLOCK;
        return hasSpecialRegen ? 20 : 1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        if (!Screen.hasShiftDown()) {
            list.add(new TranslationTextComponent(MOD_ID + ".message.hold_key", "SHIFT").appendSibling(new StringTextComponent(" ")).appendSibling(new TranslationTextComponent(MOD_ID + ".message.for_more_infos")));
        } else {
            list.add(new TranslationTextComponent(MOD_ID + ".item.enchanted_broom.desc1"));
            list.add(new TranslationTextComponent(MOD_ID + ".item.enchanted_broom.desc2"));
            list.add(new TranslationTextComponent(MOD_ID + ".item.enchanted_broom.desc3", Helper.getNameForKeybindSneak()));
        }
    }

    public static ItemStack setHeadType(ItemStack stack, int headType) {
        return NBTStackHelper.setInteger(stack, "head_type", headType);
    }

    public static int getHeadType(ItemStack stack) {
        return NBTStackHelper.getInteger(stack, "head_type", 0);
    }

    @Override
    void onEntitySpawn(ItemStack stack, EntityAbstractFlyingThing entity) {
        ((EntityEnchantedBroom) entity).setHeadType(getHeadType(stack));
    }
}
