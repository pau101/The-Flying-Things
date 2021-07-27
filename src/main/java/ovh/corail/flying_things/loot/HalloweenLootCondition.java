package ovh.corail.flying_things.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.registry.ModSerializers;

public class HalloweenLootCondition implements ILootCondition {

	@Override
	public boolean test(LootContext t) {
		return Helper.isDateAroundHalloween();
	}

	@Override
	public LootConditionType getConditionType() {
		return ModSerializers.IS_HALLOWEEN;
	}
	
	public static class Serializer implements ILootSerializer<HalloweenLootCondition> {

		@Override
		public void serialize(JsonObject obj, HalloweenLootCondition condition, JsonSerializationContext context) {

		}

		@Override
		public HalloweenLootCondition deserialize(JsonObject obj, JsonDeserializationContext context) {
			return new HalloweenLootCondition();
		}
		
	}

}
