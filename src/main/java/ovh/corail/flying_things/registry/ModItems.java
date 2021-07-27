package ovh.corail.flying_things.registry;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import ovh.corail.flying_things.item.ItemEnchantedBroom;
import ovh.corail.flying_things.item.ItemMagicCarpet;
import ovh.corail.flying_things.item.ItemPhialOfAnimation;
import ovh.corail.flying_things.item.ItemPumpkinStick;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Item.class, MOD_ID);
	
	public static final RegistryObject<ItemEnchantedBroom> enchantedBroom = ITEMS.register("enchanted_broom", () -> new ItemEnchantedBroom());
	
	public static final RegistryObject<ItemMagicCarpet> magicCarpet = ITEMS.register("magic_carpet", () -> new ItemMagicCarpet());
	
	public static final RegistryObject<ItemPhialOfAnimation> phialOfAnimation = ITEMS.register("phial_of_animation", () -> new ItemPhialOfAnimation());
	
	public static final RegistryObject<ItemPumpkinStick> pumpkinStick = ITEMS.register("pumpkin_stick", () -> new ItemPumpkinStick());
}
