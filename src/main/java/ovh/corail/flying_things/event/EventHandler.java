package ovh.corail.flying_things.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.ItemHandlerHelper;
import ovh.corail.flying_things.ConfigFlyingThings;
import ovh.corail.flying_things.ModFlyingThings;
import ovh.corail.flying_things.entity.EntityAbstractFlyingThing;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.registry.ModItems;

import java.lang.reflect.Field;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {
    private static ResourceLocation LOOTTABLE_SPECIAL = new ResourceLocation(MOD_ID, "special");
    private static final Field fieldIsFrozen = ObfuscationReflectionHelper.findField(LootTable.class, "isFrozen");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(LootTables.GAMEPLAY_FISHING)) {
            MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
            server.deferTask(() -> {
                LootTable specialTable = event.getLootTableManager().getLootTableFromLocation(LOOTTABLE_SPECIAL);
                if (specialTable == LootTable.EMPTY_LOOT_TABLE) {
                    ModFlyingThings.LOGGER.warn("The loottable for flying things is absent");
                } else {
                    // adds loots in structure chests
                    LootPool chestTreasure = specialTable.getPool("flying_things:chest_treasure");
                    LootTable targetTable;
                    for (String targetTableString : ConfigFlyingThings.general.treasureLootTable.get()) {
                        targetTable = event.getLootTableManager().getLootTableFromLocation(new ResourceLocation(targetTableString));
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
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBossDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (Helper.isBoss(entity)) {
            if (Helper.getRandom(1, 100) <= ConfigFlyingThings.general.chanceDropPhialOfAnimationOnBoss.get()) {
                event.getDrops().add(new ItemEntity(entity.world, entity.posX, entity.posY, entity.posZ, new ItemStack(ModItems.phialOfAnimation)));
            }
        } else {
            if (entity instanceof MonsterEntity && Helper.isDateAroundHalloween() && event.getSource() != null && event.getSource().getTrueSource() instanceof PlayerEntity) {
                if (Helper.getRandom(1, 100) <= ConfigFlyingThings.general.chanceDropPumpkinStick.get()) {
                    event.getDrops().add(new ItemEntity(entity.world, entity.posX, entity.posY, entity.posZ, new ItemStack(ModItems.pumpkinStick)));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingFall(LivingFallEvent event) {
        if (Helper.isRidingFlyingThing(event.getEntityLiving())) {
            event.setDistance(0f);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingInWall(LivingDamageEvent event) {
        if (event.getSource() == DamageSource.IN_WALL && Helper.isRidingFlyingThing(event.getEntityLiving())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getEntity() == null) {
            return;
        }
        Entity rider = event.getEntity();
        if (Helper.isRidingFlyingThing(rider) && ConfigFlyingThings.general.chanceToFallWithProjectile.get() != 0 && Helper.getRandom(1, 100) < ConfigFlyingThings.general.chanceToFallWithProjectile.get()) {
            rider.stopRiding();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (Helper.isControllingFlyingThing(event.getEntityLiving())) {
            EntityAbstractFlyingThing flying_thing = (EntityAbstractFlyingThing) event.getEntityLiving().getRidingEntity();
            if (flying_thing != null) {
                if (Helper.isValidPlayer(event.getEntityLiving()) && flying_thing.hasSoulbound()) {
                    ItemHandlerHelper.giveItemToPlayer((PlayerEntity) event.getEntityLiving(), flying_thing.getStack());
                } else {
                    event.getEntityLiving().entityDropItem(flying_thing.getStack(), 0f);
                }
                flying_thing.remove();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        // store the flying thing in the player inventory even when disconnected
        if (Helper.isValidPlayerMP(event.getPlayer()) && Helper.isControllingFlyingThing(event.getPlayer())) {
            Entity mount = event.getPlayer().getRidingEntity();
            if (mount != null) {
                event.getPlayer().stopRiding();
                ((Chunk) event.getPlayer().world.getChunk(mount.chunkCoordX, mount.chunkCoordZ, ChunkStatus.FULL)).markDirty();
            }
        }
    }
}
