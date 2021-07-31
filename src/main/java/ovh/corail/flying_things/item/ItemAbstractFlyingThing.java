package ovh.corail.flying_things.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.entity.EntityAbstractFlyingThing;
import ovh.corail.flying_things.helper.NBTStackHelper;

public abstract class ItemAbstractFlyingThing extends ItemGeneric {
    private static final ResourceLocation SOULBOUND_LOCATION = new ResourceLocation("tombstone", "soulbound");

    abstract EntityType<?> getEntityType();

    abstract boolean canFlyInDimension(RegistryKey<World> dimType);

    abstract void onEntitySpawn(ItemStack stack, EntityAbstractFlyingThing entity);

    ItemAbstractFlyingThing(String name, Properties builder) {
        super(name, builder);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isPassenger() || player.getCooldowns().isOnCooldown(this)) {
            return ActionResult.fail(stack);
        }
        if (stack.getItem() != this) {
            return ActionResult.pass(stack);
        }
        if (!world.isClientSide) {
            MinecraftServer server = player.level.getServer();
            if (server != null && !server.isFlightAllowed()) {
                player.sendMessage(new TranslationTextComponent("flying_things.message.flight_not_allowed"), Util.NIL_UUID);
                return ActionResult.fail(stack);
            }
            if (!canFlyInDimension(world.dimension())) {
                player.sendMessage(new TranslationTextComponent("flying_things.message.denied_dimension_to_fly", stack.getHoverName()), Util.NIL_UUID);
                return ActionResult.fail(stack);
            }
            player.getCooldowns().addCooldown(this, 100);
            EntityAbstractFlyingThing entity;
            try {
                entity = (EntityAbstractFlyingThing) getEntityType().create(world);
            } catch (Exception e) {
                return ActionResult.fail(stack);
            }
            if (entity != null) {
                entity.setModelType(getModelType(player.getItemInHand(hand)));
                entity.setEnergy(getEnergy(stack));
                entity.setSoulbound(hasSoulbound(stack));
                if (stack.hasCustomHoverName()) {
                    entity.setCustomName(stack.getHoverName());
                }
                entity.absMoveTo(player.getX(), player.getY(), player.getZ(), player.yRot, 0f);
                entity.setYHeadRot(player.yRot);
                onEntitySpawn(stack, entity);
                world.addFreshEntity(entity);
                if (!player.isCrouching()) {
                    player.startRiding(entity);
                }
                player.setItemInHand(hand, ItemStack.EMPTY);
            }
        }
        return ActionResult.success(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getEnergy(stack) < ConfigFlyingThings.shared_datas.maxEnergy.get();
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRgb(Math.max(0f, (float) (1f - getDurabilityForDisplay(stack))) / 1.5f, 1f, 1f);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1f - (double) getEnergy(stack) / (double) ConfigFlyingThings.shared_datas.maxEnergy.get();
    }

    public static void setEnergy(ItemStack stack, int energy) {
        if (stack.getItem() instanceof ItemAbstractFlyingThing) {
            NBTStackHelper.setInteger(stack, "energy", MathHelper.clamp(energy, 0, ConfigFlyingThings.shared_datas.maxEnergy.get()));
        }
    }

    private static int getEnergy(ItemStack stack) {
        if (stack.getItem() instanceof ItemAbstractFlyingThing) {
            if (NBTStackHelper.hasKeyName(stack, "energy")) {
                return MathHelper.clamp(NBTStackHelper.getInteger(stack, "energy"), 0, ConfigFlyingThings.shared_datas.maxEnergy.get());
            } else {
                setEnergy(stack, ConfigFlyingThings.shared_datas.maxEnergy.get());
                return ConfigFlyingThings.shared_datas.maxEnergy.get();
            }
        }
        return 0;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (!world.isClientSide && getEnergy(stack) < ConfigFlyingThings.shared_datas.maxEnergy.get() && entity.tickCount % (ConfigFlyingThings.general.timeToRecoverEnergy.get() * 20) == 0) {
            setEnergy(stack, getEnergy(stack) + getActualRegen(stack, world, entity, slot, isSelected));
        }
    }

    public int getActualRegen(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        return 1;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == null) {
            return false;
        }
        assert enchantment.getRegistryName() != null;
        return ConfigFlyingThings.shared_datas.allowTombstoneSoulbound.get() && stack.getEnchantmentTags().size() == 0 && enchantment.getRegistryName().equals(SOULBOUND_LOCATION);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return ConfigFlyingThings.shared_datas.allowTombstoneSoulbound.get() && stack.getEnchantmentTags().size() == 0 && EnchantedBookItem.getEnchantments(book).size() == 1 && hasSoulbound(book);
    }

    public static void setSoulbound(ItemStack stack) {
        if (!ConfigFlyingThings.shared_datas.allowTombstoneSoulbound.get()) {
            return;
        }
        Enchantment soulbound = ForgeRegistries.ENCHANTMENTS.getValue(SOULBOUND_LOCATION);
        if (soulbound == null) {
            return;
        }
        stack.enchant(soulbound, 1);
    }

    private static boolean hasSoulbound(ItemStack stack) {
        if (!ConfigFlyingThings.shared_datas.allowTombstoneSoulbound.get()) {
            return false;
        }
        Enchantment soulbound = ForgeRegistries.ENCHANTMENTS.getValue(SOULBOUND_LOCATION);
        if (soulbound == null) {
            return false;
        }
        for (Enchantment enchant : EnchantmentHelper.getEnchantments(stack).keySet()) {
            if (SOULBOUND_LOCATION.equals(enchant.getRegistryName())) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack setModelType(ItemStack stack, int modelType) {
        return NBTStackHelper.setInteger(stack, "model_type", modelType);
    }

    public static int getModelType(ItemStack stack) {
        return NBTStackHelper.getInteger(stack, "model_type", 0);
    }
}
