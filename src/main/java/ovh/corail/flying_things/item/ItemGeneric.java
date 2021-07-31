package ovh.corail.flying_things.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.flying_things.helper.NBTStackHelper;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;
import static ovh.corail.flying_things.registry.ModTabs.tabFlyingThings;

public class ItemGeneric extends Item {
    protected final String name;
    private boolean hasEffect = false;

    public ItemGeneric(String name) {
        this(name, getBuilder(true));
    }

    public ItemGeneric(String name, boolean hasTab) {
        this(name, getBuilder(hasTab));
    }

    public ItemGeneric(String name, Properties builder) {
        super(builder);
        this.name = name;
    }

    public String getSimpleName() {
        return this.name;
    }

    public ItemGeneric withEffect() {
        this.hasEffect = true;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack stack) {
        return this.hasEffect || NBTStackHelper.getBoolean(stack, "enchant");
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return MOD_ID + ".item." + this.name;
    }

    static Properties getBuilder(boolean hasTab) {
        return new Properties().tab(hasTab ? tabFlyingThings : null).stacksTo(64);
    }
}
