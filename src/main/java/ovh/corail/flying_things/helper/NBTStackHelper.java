package ovh.corail.flying_things.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

public class NBTStackHelper {

    public static ItemStack setBoolean(ItemStack stack, String keyName, boolean keyValue) {
        stack.getOrCreateTag().putBoolean(keyName, keyValue);
        return stack;
    }

    public static boolean getBoolean(ItemStack stack, String keyName) {
        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            if (tag.contains(keyName, Constants.NBT.TAG_BYTE)) {
                return tag.getBoolean(keyName);
            }
        }
        return false;
    }

    public static ItemStack setInteger(ItemStack stack, String keyName, int keyValue) {
        stack.getOrCreateTag().putInt(keyName, keyValue);
        return stack;
    }

    public static int getInteger(ItemStack stack, String keyName) {
        return getInteger(stack, keyName, Integer.MIN_VALUE);
    }

    public static int getInteger(ItemStack stack, String keyName, int defaultValue) {
        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            if (tag.contains(keyName, Constants.NBT.TAG_INT)) {
                return tag.getInt(keyName);
            }
        }
        return defaultValue;
    }

    public static ItemStack setString(ItemStack stack, String keyName, String keyValue) {
        stack.getOrCreateTag().putString(keyName, keyValue);
        return stack;
    }

    public static String getString(ItemStack stack, String keyName) {
        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            if (tag.contains(keyName, Constants.NBT.TAG_STRING)) {
                return tag.getString(keyName);
            }
        }
        return "";
    }

    public static boolean hasKeyName(ItemStack stack, String keyName) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.contains(keyName);
    }

    public static boolean removeKeyName(ItemStack stack, String keyName) {
        CompoundNBT tag = stack.getTag();
        boolean removed = false;
        if (tag != null && tag.contains(keyName)) {
            removed = true;
            tag.remove(keyName);
            if (tag.isEmpty()) {
                stack.setTag(null);
            }
        }
        return removed;
    }
}
