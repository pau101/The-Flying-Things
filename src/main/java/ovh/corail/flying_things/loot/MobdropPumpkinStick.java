package ovh.corail.flying_things.loot;

import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.helper.Helper;

public class MobdropPumpkinStick extends LootModifier {

	private final Item item;

	public MobdropPumpkinStick(ILootCondition[] conditionsIn, Item item) {
		super(conditionsIn);
		this.item = item;
	}

	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		if(Helper.isDateAroundHalloween()) {
			int val = context.getRandom().nextInt(1000);
			if(val < ConfigFlyingThings.general.chanceDropPumpkinStick.get()) {
				generatedLoot.add(new ItemStack(item));		
			}
		}
		return generatedLoot;
	}
	
	public static class Serializer extends GlobalLootModifierSerializer<MobdropPumpkinStick> {

		@Override
		public MobdropPumpkinStick read(ResourceLocation location, JsonObject object, ILootCondition[] ilootcondition) {
			Item item = JSONUtils.getItem(object, "item");
			return new MobdropPumpkinStick(ilootcondition, item);
		}

		@Override
		public JsonObject write(MobdropPumpkinStick instance) {
			JsonObject obj = this.makeConditions(instance.conditions);
			obj.addProperty("item", instance.item.getRegistryName().toString());
			return obj;
		}
		
	}
}
