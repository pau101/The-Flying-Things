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

public class MobdropPhial extends LootModifier {
	
	private final Item item;

	public MobdropPhial(ILootCondition[] conditionsIn, Item item) {
		super(conditionsIn);
		this.item = item;
	}

	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		generatedLoot.add(new ItemStack(item));
		return generatedLoot;
	}
	
	public static class Serializer extends GlobalLootModifierSerializer<MobdropPhial> {

		@Override
		public MobdropPhial read(ResourceLocation location, JsonObject object, ILootCondition[] ilootcondition) {
			Item item = JSONUtils.getItem(object, "item");
			return new MobdropPhial(ilootcondition, item);
		}

		@Override
		public JsonObject write(MobdropPhial instance) {
			JsonObject obj = this.makeConditions(instance.conditions);
			obj.addProperty("item", instance.item.getRegistryName().toString());
			return obj;
		}
		
	}

}
