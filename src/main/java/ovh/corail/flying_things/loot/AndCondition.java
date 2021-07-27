package ovh.corail.flying_things.loot;

import java.util.function.Predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.util.JSONUtils;
import ovh.corail.flying_things.registry.ModSerializers;

public class AndCondition implements ILootCondition {

	private final ILootCondition[] conditions;
	private final Predicate<LootContext> contextPredicate;

	public AndCondition(ILootCondition[] conditionsIn) {
		this.conditions = conditionsIn;
		this.contextPredicate = LootConditionManager.and(conditionsIn);
	}

	@Override
	public boolean test(LootContext t) {
		return this.contextPredicate.test(t);
	}

	@Override
	public LootConditionType getConditionType() {
		return ModSerializers.AND;
	}
	
	public static class Serializer implements ILootSerializer<AndCondition> {

		@Override
		public void serialize(JsonObject obj, AndCondition condition, JsonSerializationContext context) {
			obj.add("terms", context.serialize(condition.conditions));
		}

		@Override
		public AndCondition deserialize(JsonObject obj, JsonDeserializationContext context) {
			ILootCondition[] ailootcondition = JSONUtils.deserializeClass(obj, "terms", context, ILootCondition[].class);
	         return new AndCondition(ailootcondition);
		}
		
	}

}
