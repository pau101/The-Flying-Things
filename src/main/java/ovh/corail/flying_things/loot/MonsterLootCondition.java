package ovh.corail.flying_things.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import ovh.corail.flying_things.registry.ModSerializers;

public class MonsterLootCondition implements ILootCondition {

	@Override
	public boolean test(LootContext t) {
		return t.getParamOrNull(LootContext.EntityTarget.THIS.getParam()) instanceof MonsterEntity;
	}

	@Override
	public LootConditionType getType() {
		return ModSerializers.IS_MONSTER;
	}
	
	public static class Serializer implements ILootSerializer<MonsterLootCondition> {

		@Override
		public void serialize(JsonObject obj, MonsterLootCondition condition, JsonSerializationContext context) {

		}

		@Override
		public MonsterLootCondition deserialize(JsonObject obj, JsonDeserializationContext context) {
			return new MonsterLootCondition();
		}
		
	}

}
