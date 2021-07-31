package ovh.corail.flying_things.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

public class ItemPumpkinStick extends ItemGeneric {

    public ItemPumpkinStick() {
        super("pumpkin_stick", getBuilder(true).stacksTo(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        list.add(new TranslationTextComponent(MOD_ID + ".item." + name + ".desc"));
    }
}
