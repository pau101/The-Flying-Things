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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistries;
import ovh.corail.flying_things.ConfigFlyingThings;
import ovh.corail.flying_things.entity.EntityAbstractFlyingThing;
import ovh.corail.flying_things.helper.NBTStackHelper;

public abstract class ItemAbstractFlyingThing extends ItemGeneric {
    private static final ResourceLocation SOULBOUND_LOCATION = new ResourceLocation("tombstone", "soulbound");

    abstract EntityType<?> getEntityType();

    abstract boolean canFlyInDimension(DimensionType dimType);

    abstract void onEntitySpawn(ItemStack stack, EntityAbstractFlyingThing entity);

    ItemAbstractFlyingThing(String name, Properties builder) {
        super(name, builder);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isPassenger() || player.getCooldownTracker().hasCooldown(this)) {
            return ActionResult.newResult(ActionResultType.FAIL, stack);
        }
        if (stack.getItem() != this) {
            return ActionResult.newResult(ActionResultType.PASS, stack);
        }
        if (!world.isRemote) {
            MinecraftServer server = player.world.getServer();
            if (server != null && !server.isFlightAllowed()) {
                player.sendMessage(new TranslationTextComponent("flying_things.message.flight_not_allowed"));
                return ActionResult.newResult(ActionResultType.FAIL, stack);
            }
            if (!canFlyInDimension(world.dimension.getType())) {
                player.sendMessage(new TranslationTextComponent("flying_things.message.denied_dimension_to_fly", stack.getDisplayName()));
                return ActionResult.newResult(ActionResultType.FAIL, stack);
            }
            player.getCooldownTracker().setCooldown(this, 100);
            EntityAbstractFlyingThing entity;
            try {
                entity = (EntityAbstractFlyingThing) getEntityType().create(world);
            } catch (Exception e) {
                return ActionResult.newResult(ActionResultType.FAIL, stack);
            }
            if (entity != null) {
                entity.setModelType(getModelType(player.getHeldItem(hand)));
                entity.setEnergy(getEnergy(stack));
                entity.setSoulbound(hasSoulbound(stack));
                if (stack.hasDisplayName()) {
                    entity.setCustomName(stack.getDisplayName());
                }
                entity.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 0f);
                entity.setRotationYawHead(player.rotationYaw);
                onEntitySpawn(stack, entity);
                world.addEntity(entity);
                if (!player.isSneaking()) {
                    player.startRiding(entity);
                }
                player.setHeldItem(hand, ItemStack.EMPTY);
            }
        }
        return ActionResult.newResult(ActionResultType.SUCCESS, stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getEnergy(stack) < ConfigFlyingThings.General.getMaxEnergy();
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(0f, (float) (1f - getDurabilityForDisplay(stack))) / 1.5f, 1f, 1f);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1f - (double) getEnergy(stack) / (double) ConfigFlyingThings.General.getMaxEnergy();
    }

    public static void setEnergy(ItemStack stack, int energy) {
        if (stack.getItem() instanceof ItemAbstractFlyingThing) {
            NBTStackHelper.setInteger(stack, "energy", MathHelper.clamp(energy, 0, ConfigFlyingThings.General.getMaxEnergy()));
        }
    }

    private static int getEnergy(ItemStack stack) {
        if (stack.getItem() instanceof ItemAbstractFlyingThing) {
            if (NBTStackHelper.hasKeyName(stack, "energy")) {
                return MathHelper.clamp(NBTStackHelper.getInteger(stack, "energy"), 0, ConfigFlyingThings.General.getMaxEnergy());
            } else {
                setEnergy(stack, ConfigFlyingThings.General.getMaxEnergy());
                return ConfigFlyingThings.General.getMaxEnergy();
            }
        }
        return 0;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (!world.isRemote && getEnergy(stack) < ConfigFlyingThings.General.getMaxEnergy() && entity.ticksExisted % (ConfigFlyingThings.general.timeToRecoverEnergy.get() * 20) == 0) {
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
        return ConfigFlyingThings.General.isSoulboundAllowed() && stack.getEnchantmentTagList().size() == 0 && enchantment.getRegistryName().equals(SOULBOUND_LOCATION);
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return ConfigFlyingThings.General.isSoulboundAllowed() && stack.getEnchantmentTagList().size() == 0 && EnchantedBookItem.getEnchantments(book).size() == 1 && hasSoulbound(book);
    }

    public static void setSoulbound(ItemStack stack) {
        if (!ConfigFlyingThings.General.isSoulboundAllowed()) {
            return;
        }
        Enchantment soulbound = ForgeRegistries.ENCHANTMENTS.getValue(SOULBOUND_LOCATION);
        if (soulbound == null) {
            return;
        }
        stack.addEnchantment(soulbound, 1);
    }

    private static boolean hasSoulbound(ItemStack stack) {
        if (!ConfigFlyingThings.General.isSoulboundAllowed()) {
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
