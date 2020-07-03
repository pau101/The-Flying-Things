package ovh.corail.flying_things.helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import ovh.corail.flying_things.ConfigFlyingThings;
import ovh.corail.flying_things.registry.ModEntities;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Random;

public class Helper {
    @Nullable
    private static Boolean isHalloween = null;

    private static final Random random = new Random();

    public static int getRandom(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static boolean isValidPlayer(Entity entity) {
        return entity instanceof PlayerEntity;
    }

    public static boolean isValidPlayerMP(Entity entity) {
        return isValidPlayer(entity) && !entity.world.isRemote;
    }

    public static boolean isFlyingthing(@Nullable Entity entity) {
        return entity != null && (entity.getType() == ModEntities.enchanted_broom || entity.getType() == ModEntities.magic_carpet);
    }

    public static boolean isBoss(Entity entity) {
        return entity != null && !entity.isNonBoss();
    }

    public static boolean isRidingFlyingThing(@Nullable Entity entity) {
        return entity != null && isFlyingthing(entity.getRidingEntity());
    }

    public static boolean isControllingFlyingThing(@Nullable Entity entity) {
        return isRidingFlyingThing(entity) && entity.getRidingEntity().getControllingPassenger() == entity;
    }

    public static String getDimensionString(DimensionType dimensionType) {
        ResourceLocation rl = DimensionType.getKey(dimensionType);
        return rl == null ? "" : rl.toString();
    }

    public static boolean isDateAroundHalloween() {
        if (ConfigFlyingThings.general.persistantHolidays.get()) {
            return true;
        }
        if (isHalloween == null) {
            LocalDate date = LocalDate.now();
            isHalloween = date.get(ChronoField.MONTH_OF_YEAR) + 1 == 10 && date.get(ChronoField.DAY_OF_MONTH) >= 20 || date.get(ChronoField.MONTH_OF_YEAR) + 1 == 11 && date.get(ChronoField.DAY_OF_MONTH) <= 3;
        }
        return isHalloween;
    }
}
