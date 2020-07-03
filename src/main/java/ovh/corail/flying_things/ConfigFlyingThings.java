package ovh.corail.flying_things;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;
import ovh.corail.flying_things.network.UpdateClientMessage;

import java.util.ArrayList;
import java.util.List;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@SuppressWarnings({ "unused", "WeakerAccess" })
public class ConfigFlyingThings {

    public static class General {
        public final ForgeConfigSpec.ConfigValue<Integer> speedMax;
        public final ForgeConfigSpec.ConfigValue<Integer> accelerationMax;
        public final ForgeConfigSpec.ConfigValue<Integer> accelerationIncrement;
        public final ForgeConfigSpec.ConfigValue<Integer> speedMaxNoEnergy;
        public final ForgeConfigSpec.ConfigValue<Integer> maxEnergy;
        public final ForgeConfigSpec.ConfigValue<Integer> timeToLoseEnergy;
        public final ForgeConfigSpec.ConfigValue<Integer> timeToRecoverEnergy;
        public final ForgeConfigSpec.ConfigValue<Boolean> allowTombstoneSoulbound;
        public final ForgeConfigSpec.ConfigValue<Boolean> allowToFlyInWater;
        public final ForgeConfigSpec.ConfigValue<Boolean> allowToBreakPlant;
        public final ForgeConfigSpec.ConfigValue<Boolean> allowSpecialRegen;
        public final ForgeConfigSpec.ConfigValue<Integer> chanceToFallWithProjectile;
        public final ForgeConfigSpec.ConfigValue<Boolean> persistantHolidays;
        public final ForgeConfigSpec.ConfigValue<Integer> chanceDropPhialOfAnimationOnBoss;
        public final ForgeConfigSpec.ConfigValue<Integer> chanceDropPumpkinStick;
        public final ForgeConfigSpec.ConfigValue<List<String>> treasureLootTable;

        public static int serverSpeedMax, serverAccelerationMax, serverAccelerationIncrement, serverSpeedMaxNoEnergy, serverMaxEnergy;
        public static boolean serverAllowTombstoneSoulbound;

        public static int getSpeedMax() {
            return serverSpeedMax;
        }

        public static int getAccelerationMax() {
            return serverAccelerationMax;
        }

        public static int getAccelerationIncrement() {
            return serverAccelerationIncrement;
        }

        public static int getSpeedMaxNoEnergy() {
            return serverSpeedMaxNoEnergy;
        }

        public static int getMaxEnergy() {
            return serverMaxEnergy;
        }

        public static boolean isSoulboundAllowed() {
            return serverAllowTombstoneSoulbound;
        }

        public General(ForgeConfigSpec.Builder builder) {
            builder.comment("Theses options can only be modified on the server in multiplayer").push("general");
            speedMax = builder
                    .comment("Maximum Speed [0..200|default:90]")
                    .translation(getTranslation("speed_max"))
                    .defineInRange("speed_max", 90, 0, 200);
            accelerationMax = builder
                    .comment("Maximum acceleration [0..100|default:35]")
                    .translation(getTranslation("acceleration_max"))
                    .defineInRange("acceleration_max", 35, 0, 100);
            accelerationIncrement = builder
                    .comment("Acceleration increment [1..20|default:7]")
                    .translation(getTranslation("acceleration_increment"))
                    .defineInRange("acceleration_increment", 7, 1, 20);
            speedMaxNoEnergy = builder
                    .comment("Maximum speed with no energy [0..300|default:30]")
                    .translation(getTranslation("speed_max_no_energy"))
                    .defineInRange("speed_max_no_energy", 30, 0, 300);
            maxEnergy = builder
                    .comment("Maximum energy [100..MAX|default:1000]")
                    .translation(getTranslation("max_energy"))
                    .defineInRange("max_energy", 1000, 100, Integer.MAX_VALUE);
            timeToLoseEnergy = builder
                    .comment("Time to lose an energy point [5..MAX|default:5]")
                    .translation(getTranslation("time_to_lose_energy"))
                    .defineInRange("time_to_lose_energy", 5, 5, Integer.MAX_VALUE);
            timeToRecoverEnergy = builder
                    .comment("Time to recover an energy point [5..MAX|default:5]")
                    .translation(getTranslation("time_to_recover_energy"))
                    .defineInRange("timeToRecoverEnergy", 5, 5, Integer.MAX_VALUE);
            allowTombstoneSoulbound = builder
                    .comment("Allow applying Soulbound [false/true|default:true]")
                    .translation(getTranslation("allow_tombstone_soulbound"))
                    .define("allow_tombstone_soulbound", true);
            allowToFlyInWater = builder
                    .comment("Allow flying in water [false/true|default:true]")
                    .translation(getTranslation("allow_to_fly_in_water"))
                    .define("allow_to_fly_in_water", true);
            allowToBreakPlant = builder
                    .comment("Allow breaking plants [false/true|default:true]")
                    .translation(getTranslation("allow_to_break_plant"))
                    .define("allow_to_break_plant", true);
            allowSpecialRegen = builder
                    .comment("Allow special regeneration [false/true|default:true]")
                    .translation(getTranslation("allow_special_regen"))
                    .define("allow_special_regen", true);
            chanceToFallWithProjectile = builder
                    .comment("Chance to fall with projectiles [0..100|default:10]")
                    .translation(getTranslation("chance_to_fall_with_projectile"))
                    .defineInRange("chance_to_fall_with_projectile", 10, 0, 100);
            persistantHolidays = builder
                    .comment("Allow special holiday events outside periods [false/true|default:true]")
                    .translation(getTranslation("persistant_holidays"))
                    .define("persistant_holidays", true);
            chanceDropPumpkinStick = builder
                    .comment("Chance to get Halloween Sticks during this event [0..1000|default:10]")
                    .translation(getTranslation("chance_drop_pumpkin_stick"))
                    .defineInRange("chance_drop_pumpkin_stick", 10, 0, 1000);
            chanceDropPhialOfAnimationOnBoss = builder
                    .comment("Chance to get a Phial of Animation on boss [0..1000|default:10]")
                    .translation(getTranslation("chance_drop_phial_of_animation_on_boss"))
                    .defineInRange("chance_drop_phial_of_animation_on_boss", 10, 0, 1000);
            treasureLootTable = builder
                    .comment("Defines the loottables having a chance to contain a Phial of Animation")
                    .translation(getTranslation("treasure_loot_table"))
                    .define("treasure_loot_table", Lists.newArrayList("minecraft:chests/end_city_treasure", "minecraft:chests/abandoned_mineshaft", "minecraft:chests/nether_bridge", "minecraft:chests/stronghold_library", "minecraft:chests/desert_pyramid", "minecraft:chests/jungle_temple", "minecraft:chests/igloo_chest", "minecraft:chests/woodland_mansion"));

            builder.pop();
        }
    }

    public static class Client {
        public final ForgeConfigSpec.ConfigValue<Boolean> renderEffect;
        public final ForgeConfigSpec.ConfigValue<Integer> barLength;
        public final ForgeConfigSpec.ConfigValue<Integer> barHeightPos;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Theses options are custom parameters for the player").push("client");
            renderEffect = builder
                    .comment("Enable rendering effects [false/true|default:true]")
                    .translation(getTranslation("render_effect"))
                    .define("render_effect", true);
            barLength = builder
                    .comment("Length of the bars [9..182|default:182]")
                    .translation(getTranslation("bar_length"))
                    .defineInRange("bar_length", 182, 9, 182);
            barHeightPos = builder
                    .comment("Height of the position of the bars [0..MAX|default:62]")
                    .translation(getTranslation("bar_height_pos"))
                    .defineInRange("bar_height_pos", 62, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class DeniedDimensionToFly {
        public final ForgeConfigSpec.ConfigValue<List<String>> deniedDimensionBroom;
        public final ForgeConfigSpec.ConfigValue<List<String>> deniedDimensionCarpet;

        public DeniedDimensionToFly(ForgeConfigSpec.Builder builder) {
            builder.comment("Denied dimensions to ride a flying thing - example : minecraft:the_nether").push("denied_dimensions_to_fly");
            deniedDimensionBroom = builder
                    .comment("Dimensions for the brooms")
                    .translation(getTranslation("denied_dimension_broom"))
                    .define("denied_dimension_broom", new ArrayList<>());
            deniedDimensionCarpet = builder
                    .comment("Dimensions for the carpets")
                    .translation(getTranslation("denied_dimension_carpet"))
                    .define("denied_dimension_carpet", new ArrayList<>());
            builder.pop();
        }
    }

    private static String getTranslation(String name) {
        return MOD_ID + ".config." + name;
    }

    static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client client;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specPair.getRight();
        client = specPair.getLeft();
    }

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General general = new General(BUILDER);
    public static final DeniedDimensionToFly deniedDimensionToFly = new DeniedDimensionToFly(BUILDER);

    static final ForgeConfigSpec GENERAL_SPEC = BUILDER.build();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MOD_ID)) {
            //ConfigManager.sync(MOD_ID, Type.INSTANCE);
            if (Minecraft.getInstance().isSingleplayer()) {
                updateServerDatas();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerLogued(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() != null && event.getPlayer().isServerWorld()) {
            ModFlyingThings.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new UpdateClientMessage(general.speedMax.get(), general.accelerationMax.get(), general.accelerationIncrement.get(), general.speedMaxNoEnergy.get(), general.maxEnergy.get(), general.allowTombstoneSoulbound.get()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote()) {
            updateServerDatas();
        }
    }

    private static void updateServerDatas() {
        General.serverSpeedMax = general.speedMax.get();
        General.serverAccelerationMax = general.accelerationMax.get();
        General.serverAccelerationIncrement = general.accelerationIncrement.get();
        General.serverSpeedMaxNoEnergy = general.speedMaxNoEnergy.get();
        General.serverMaxEnergy = general.maxEnergy.get();
        General.serverAllowTombstoneSoulbound = general.allowTombstoneSoulbound.get();
    }
}
