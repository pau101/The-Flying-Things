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

public class LootTableAdder extends LootModifier {
	
	private final Item item;
	private final int chance;

	public LootTableAdder(ILootCondition[] conditionsIn, Item item, int chance) {
		super(conditionsIn);
		this.item = item;
		this.chance = chance;
	}

	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		int val = context.getRandom().nextInt(100);
		if(val < chance) {
			generatedLoot.add(new ItemStack(item, 1));
		}
		return generatedLoot;
	}
	
	public static class Serializer extends GlobalLootModifierSerializer<LootTableAdder> {

		@Override
		public LootTableAdder read(ResourceLocation location, JsonObject object, ILootCondition[] ilootcondition) {
			int chance = JSONUtils.getInt(object, "chance");
			Item item = JSONUtils.getItem(object, "item");
			return new LootTableAdder(ilootcondition, item, chance);
		}

		@Override
		public JsonObject write(LootTableAdder instance) {
			JsonObject obj = this.makeConditions(instance.conditions);
			obj.addProperty("chance", instance.chance);
			obj.addProperty("item", instance.item.getRegistryName().toString());
			return obj;
		}
		
	}

}
