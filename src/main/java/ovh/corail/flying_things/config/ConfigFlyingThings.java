package ovh.corail.flying_things.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import ovh.corail.flying_things.ModFlyingThings;
import ovh.corail.flying_things.network.UpdateConfigMessage;

import java.util.ArrayList;
import java.util.List;

import static ovh.corail.flying_things.ModFlyingThings.MOD_ID;

@SuppressWarnings({ "unused", "WeakerAccess" })
public class ConfigFlyingThings {

    public static class General {
        public final ConfigValue<Integer> timeToLoseEnergy;
        public final ConfigValue<Integer> timeToRecoverEnergy;
        public final ConfigValue<Boolean> allowToFlyInWater;
        public final ConfigValue<Boolean> allowToBreakPlant;
        public final ConfigValue<Boolean> allowSpecialRegen;
        public final ConfigValue<Integer> chanceToFallWithProjectile;
        public final ConfigValue<Boolean> persistantHolidays;
        public final ConfigValue<Integer> chanceDropPhialOfAnimationInChest;
        public final ConfigValue<Integer> chanceDropPhialOfAnimationOnBoss;
        public final ConfigValue<Integer> chanceDropPumpkinStick;
        public final ConfigValue<List<String>> treasureLootTable;

        public General(ForgeConfigSpec.Builder builder) {
            builder.comment("Theses options can only be modified on the server in multiplayer").push("general");

            timeToLoseEnergy = builder
                    .comment("Time to lose an energy point [5..MAX|default:5]")
                    .translation(getTranslation("time_to_lose_energy"))
                    .defineInRange("time_to_lose_energy", 5, 5, Integer.MAX_VALUE);
            timeToRecoverEnergy = builder
                    .comment("Time to recover an energy point [5..MAX|default:5]")
                    .translation(getTranslation("time_to_recover_energy"))
                    .defineInRange("timeToRecoverEnergy", 5, 5, Integer.MAX_VALUE);
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
            chanceDropPhialOfAnimationInChest = builder
                    .comment("Chance to get a Phial of Animation in chest [0..1000|default:50]")
                    .translation(getTranslation("chance_drop_phial_of_animation_in_chest"))
                    .defineInRange("chance_drop_phial_of_animation_in_chest", 50, 0, 1000);
            chanceDropPhialOfAnimationOnBoss = builder
                    .comment("Chance to get a Phial of Animation on boss [0..1000|default:200]")
                    .translation(getTranslation("chance_drop_phial_of_animation_on_boss"))
                    .defineInRange("chance_drop_phial_of_animation_on_boss", 200, 0, 1000);
            treasureLootTable = builder
                    .comment("Defines the loottables having a chance to contain a Phial of Animation")
                    .translation(getTranslation("treasure_loot_table"))
                    .define("treasure_loot_table", Lists.newArrayList("minecraft:chests/end_city_treasure", "minecraft:chests/abandoned_mineshaft", "minecraft:chests/nether_bridge", "minecraft:chests/stronghold_library", "minecraft:chests/desert_pyramid", "minecraft:chests/jungle_temple", "minecraft:chests/igloo_chest", "minecraft:chests/woodland_mansion"));

            builder.pop();
        }
    }

    public static class Client {
        public final ConfigValue<Boolean> renderEffect;
        public final ConfigValue<Boolean> barValue;
        public final ConfigValue<Integer> barHeightPos;
        public final ConfigValue<Integer> barColorSpeed;
        public final ConfigValue<Integer> barColorEnergy;
        public final ConfigValue<Graduation> barGraduationEnergy;
        public final ConfigValue<Graduation> barGraduationSpeed;
        public enum Graduation { SMALLEST, SMALLER, NORMAL, LARGEST }

        public Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Theses options are custom parameters for the player").push("client");
            renderEffect = builder
                    .comment("Enable rendering effects [false/true|default:true]")
                    .translation(getTranslation("render_effect"))
                    .define("render_effect", true);
            barValue = builder
                    .comment("Display the bar values [false/true|default:false]")
                    .translation(getTranslation("bar_value"))
                    .define("bar_value", false);
            barHeightPos = builder
                    .comment("Height of the position of the bars [0..100|default:62]")
                    .translation(getTranslation("bar_height_pos"))
                    .defineInRange("bar_height_pos", 62, 0, 100);
            barColorEnergy = builder
                    .comment("Color of the speed bar [0..16777215|default:65280]")
                    .translation(getTranslation("bar_color_energy"))
                    .defineInRange("bar_color_energy", 65280, 0, 16777215);
            barColorSpeed = builder
                    .comment("Color of the speed bar [0..16777215|default:16711680]")
                    .translation(getTranslation("bar_color_speed"))
                    .defineInRange("bar_color_speed", 16711680, 0, 16777215);
            barGraduationEnergy = builder
                    .comment("Type of graduation of the energy bar")
                    .translation(getTranslation("bar_graduation_energy"))
                    .defineEnum("bar_graduation_energy", Graduation.SMALLER);
            barGraduationSpeed = builder
                    .comment("Type of graduation of the speed bar")
                    .translation(getTranslation("bar_graduation_speed"))
                    .defineEnum("bar_graduation_speed", Graduation.SMALLER);
            builder.pop();
        }
    }

    public static class DeniedDimensionToFly {
        public final ConfigValue<List<String>> deniedDimensionBroom;
        public final ConfigValue<List<String>> deniedDimensionCarpet;

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

    public static class SharedDatas {
        public final ConfigValue<Integer> speedMax;
        public final ConfigValue<Integer> accelerationMax;
        public final ConfigValue<Integer> accelerationIncrement;
        public final ConfigValue<Integer> speedMaxNoEnergy;
        public final ConfigValue<Integer> maxEnergy;
        public final ConfigValue<Boolean> allowTombstoneSoulbound;

        public SharedDatas(ForgeConfigSpec.Builder builder) {
            builder.comment("Theses options are automatically sync on the client").push("shared_datas");

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
            allowTombstoneSoulbound = builder
                    .comment("Allow applying Soulbound [false/true|default:true]")
                    .translation(getTranslation("allow_tombstone_soulbound"))
                    .define("allow_tombstone_soulbound", true);

            builder.pop();
        }
    }

    private static String getTranslation(String name) {
        return MOD_ID + ".config." + name;
    }

    public static final Client client;
    public static final ForgeConfigSpec CLIENT_SPEC;
    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        client = specPair.getLeft();
        CLIENT_SPEC = specPair.getRight();
    }

    public static final General general;
    public static final DeniedDimensionToFly deniedDimensionToFly;
    public static final ForgeConfigSpec GENERAL_SPEC;
    static {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        general = new General(BUILDER);
        deniedDimensionToFly = new DeniedDimensionToFly(BUILDER);
        GENERAL_SPEC = BUILDER.build();
    }

    public static final SharedDatas shared_datas;
    public static final ForgeConfigSpec SHARED_SPEC;
    static {
        final Pair<SharedDatas, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(SharedDatas::new);
        shared_datas = specPair.getLeft();
        SHARED_SPEC = specPair.getRight();
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ConfigEvent {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onReloadConfig(ModConfig.Reloading event) {
            if (event.getConfig().getModId().equals(MOD_ID) && event.getConfig().getType() == ModConfig.Type.SERVER) {
                ModFlyingThings.PROXY.markConfigDirty();
            }
        }
    }

    public static UpdateConfigMessage getUpdatePacket() {
        int[] intConfigs = new int[IntConfigs.values().length];
        for (IntConfigs config : IntConfigs.values()) {
            intConfigs[config.ordinal()] = config.get();
        }
        return new UpdateConfigMessage(shared_datas.allowTombstoneSoulbound.get(), intConfigs);
    }

    public static void updateConfig(boolean allowTombstoneSoulbound, int[] intConfigs) {
        shared_datas.allowTombstoneSoulbound.set(allowTombstoneSoulbound);
        for (IntConfigs config : IntConfigs.values()) {
            config.set(intConfigs[config.ordinal()]);
        }
    }

    enum IntConfigs {
        speedMax(shared_datas.speedMax),
        accelerationMax(shared_datas.accelerationMax),
        accelerationIncrement(shared_datas.accelerationIncrement),
        speedMaxNoEnergy(shared_datas.speedMaxNoEnergy),
        maxEnergy(shared_datas.maxEnergy);

        private ConfigValue<Integer> supplier;

        IntConfigs(ConfigValue<Integer> supplier) {
            this.supplier = supplier;
        }

        public int get() {
            return this.supplier.get();
        }

        public void set(int value) {
            this.supplier.set(value);
        }
    }
}
