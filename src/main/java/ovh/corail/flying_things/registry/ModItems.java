package ovh.corail.flying_things.registry;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import ovh.corail.flying_things.item.ItemEnchantedBroom;
import ovh.corail.flying_things.item.ItemMagicCarpet;
import ovh.corail.flying_things.item.ItemPhialOfAnimation;
import ovh.corail.flying_things.item.ItemPumpkinStick;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

@ObjectHolder(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems extends Registrable {
    public static final ItemEnchantedBroom enchantedBroom = new ItemEnchantedBroom();
    public static final ItemMagicCarpet magicCarpet = new ItemMagicCarpet();
    public static final ItemPhialOfAnimation phialOfAnimation = new ItemPhialOfAnimation();
    public static final ItemPumpkinStick pumpkinStick = new ItemPumpkinStick();

    @SubscribeEvent
    public static void registerItemBlock(final RegistryEvent.Register<Item> event) {
        registerForgeEntry(event.getRegistry(), enchantedBroom, enchantedBroom.getSimpleName());
        registerForgeEntry(event.getRegistry(), magicCarpet, magicCarpet.getSimpleName());
        registerForgeEntry(event.getRegistry(), phialOfAnimation, phialOfAnimation.getSimpleName());
        registerForgeEntry(event.getRegistry(), pumpkinStick, pumpkinStick.getSimpleName());
    }
}
