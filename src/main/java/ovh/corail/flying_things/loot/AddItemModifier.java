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

public class AddItemModifier extends LootModifier {
	
	private final Item item;

	public AddItemModifier(ILootCondition[] conditionsIn, Item item) {
		super(conditionsIn);
		this.item = item;
	}

	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		generatedLoot.add(new ItemStack(item));
		return generatedLoot;
	}
	
	public static class Serializer extends GlobalLootModifierSerializer<AddItemModifier> {

		@Override
		public AddItemModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ilootcondition) {
			Item item = JSONUtils.getItem(object, "item");
			return new AddItemModifier(ilootcondition, item);
		}

		@Override
		public JsonObject write(AddItemModifier instance) {
			JsonObject obj = this.makeConditions(instance.conditions);
			obj.addProperty("item", instance.item.getRegistryName().toString());
			return obj;
		}
		
	}

}
