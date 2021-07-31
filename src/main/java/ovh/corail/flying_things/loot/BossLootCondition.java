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

public class BossLootCondition implements ILootCondition {

	@Override
	public boolean test(LootContext t) {
		return Helper.isBoss(t.getParamOrNull(LootContext.EntityTarget.THIS.getParam()));
	}

	@Override
	public LootConditionType getType() {
		return ModSerializers.IS_BOSS;
	}
	
	public static class Serializer implements ILootSerializer<BossLootCondition> {

		@Override
		public void serialize(JsonObject obj, BossLootCondition condition, JsonSerializationContext context) {

		}

		@Override
		public BossLootCondition deserialize(JsonObject obj, JsonDeserializationContext context) {
			return new BossLootCondition();
		}
		
	}

}
