package ovh.corail.flying_things.helper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.EmptyLootEntry;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.ModFlyingThings;
import ovh.corail.flying_things.registry.ModItems;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collection;

public class LootHelper {
    private static final Field fieldIsFrozen = ObfuscationReflectionHelper.findField(LootTable.class, "isFrozen");

    public static void addChestEntries(LootTableManager manager) {
        // adds loots in structure chests
        int weight = ConfigFlyingThings.general.chanceDropPhialOfAnimationInChest.get();
        if (weight == 0) {
            return;
        }
        LootPool.Builder builder = new LootPool.Builder().name(ModFlyingThings.MOD_ID + ":chest_treasure");

        builder.addEntry(ItemLootEntry.builder(ModItems.phialOfAnimation).quality(2).weight(weight));
        builder.addEntry(EmptyLootEntry.func_216167_a().weight(1000 - weight));
        LootPool chestTreasure = builder.build();

        for (String targetTableString : ConfigFlyingThings.general.treasureLootTable.get()) {
            LootTable targetTable = manager.getLootTableFromLocation(new ResourceLocation(targetTableString));
            if (targetTable != LootTable.EMPTY_LOOT_TABLE) {
                try {
                    fieldIsFrozen.set(targetTable, false);
                    targetTable.addPool(chestTreasure);
                    fieldIsFrozen.set(targetTable, true);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void handleMobDrops(Collection<ItemEntity> drops, LivingEntity entity, @Nullable DamageSource damageSource) {
        if (Helper.isBoss(entity)) {
            Integer chance = ConfigFlyingThings.general.chanceDropPhialOfAnimationOnBoss.get();
            if (chance > 0 && Helper.getRandom(1, 1000) <= chance) {
                drops.add(new ItemEntity(entity.world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), new ItemStack(ModItems.phialOfAnimation)));
            }
        } else {
            if (entity instanceof MonsterEntity && Helper.isDateAroundHalloween() && damageSource != null && damageSource.getTrueSource() instanceof PlayerEntity) {
                Integer chance = ConfigFlyingThings.general.chanceDropPumpkinStick.get();
                if (chance > 0 && Helper.getRandom(1, 1000) <= chance) {
                    drops.add(new ItemEntity(entity.world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), new ItemStack(ModItems.pumpkinStick)));
                }
            }
        }
    }
}
