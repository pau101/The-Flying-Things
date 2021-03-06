package ovh.corail.flying_things.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import ovh.corail.flying_things.ModFlyingThings;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.entity.EntityAbstractFlyingThing;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.helper.LootHelper;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(LootTables.GAMEPLAY_FISHING)) {
            ThreadTaskExecutor server = Helper.getServer();
            if (server == null) {
                ModFlyingThings.LOGGER.warn("A mod called the LootTableLoadEvent from the client side");
            } else {
                server.deferTask(() -> LootHelper.addChestEntries(event.getLootTableManager()));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onBossDrops(LivingDropsEvent event) {
        LootHelper.handleMobDrops(event.getDrops(), event.getEntityLiving(), event.getSource());
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
        if (ConfigFlyingThings.general.chanceToFallWithProjectile.get() > 0 && Helper.isRidingFlyingThing(event.getEntity()) && Helper.random.nextFloat() * 100 < ConfigFlyingThings.general.chanceToFallWithProjectile.get()) {
            event.getEntity().stopRiding();
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
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
}
