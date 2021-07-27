package ovh.corail.flying_things.item;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
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
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.registry.ModEntities;
import ovh.corail.flying_things.render.TEISRMagicCarpet;

import javax.annotation.Nullable;
import java.util.List;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

public class ItemMagicCarpet extends ItemAbstractFlyingThing {
    private static final int MAX_ID = 19;

    public ItemMagicCarpet() {
        super("magic_carpet", getBuilder(true).maxStackSize(1).setISTER(() -> TEISRMagicCarpet::new));
    }

    @Override
    EntityType<?> getEntityType() {
        return ModEntities.magic_carpet;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            for (int i = 0; i <= MAX_ID; i++) {
                ItemStack stack = new ItemStack(this);
                setModelType(stack, i);
                items.add(stack);
            }
        }
    }

    @Override
    public boolean canFlyInDimension(RegistryKey<World> dimensionType) {
        return !ConfigFlyingThings.deniedDimensionToFly.deniedDimensionCarpet.get().contains(Helper.getDimensionString(dimensionType));
    }

    @Override
    public int getActualRegen(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        boolean hasSpecialRegen = ConfigFlyingThings.general.allowSpecialRegen.get() && world.isBlockLoaded(entity.getPosition().down()) && world.getBlockState(entity.getPosition().down()).getBlock() == Blocks.SOUL_SAND;
        return hasSpecialRegen ? 20 : 1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        if (!Screen.hasShiftDown()) {
            list.add(new TranslationTextComponent(MOD_ID + ".message.hold_key", "SHIFT").appendSibling(new StringTextComponent(" ")).appendSibling(new TranslationTextComponent(MOD_ID + ".message.for_more_infos")));
        } else {
            list.add(new TranslationTextComponent(MOD_ID + ".item.magic_carpet.desc1"));
            list.add(new TranslationTextComponent(MOD_ID + ".item.magic_carpet.desc2", Helper.getNameForKeybindSneak()));
        }
        int id = getModelType(stack);
        if ((id == 18 || id == 19)) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null && !player.isPotionActive(Effects.HERO_OF_THE_VILLAGE)) {
                list.add(new TranslationTextComponent(MOD_ID + ".message.require_effect", new StringTextComponent("[").appendSibling(new TranslationTextComponent(Effects.HERO_OF_THE_VILLAGE.getName())).appendString("]")));
            }
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        int modelType = getModelType(stack);
        String postName = "";
        if (modelType == 18 || modelType == 19) {
            postName = ".pillage";
        } else if (modelType > 8 && modelType < 14) {
            postName = ".halloween";
        }
        return new TranslationTextComponent(getTranslationKey(stack) + postName);
    }

    @Override
    void onEntitySpawn(ItemStack stack, EntityAbstractFlyingThing entity) {
    }
}
