package ovh.corail.flying_things.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.FMLPlayMessages;
import ovh.corail.flying_things.ConfigFlyingThings;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.item.ItemAbstractFlyingThing;
import ovh.corail.flying_things.item.ItemEnchantedBroom;
import ovh.corail.flying_things.registry.ModEntities;
import ovh.corail.flying_things.registry.ModItems;

public class EntityEnchantedBroom extends EntityAbstractFlyingThing {
    private static final DataParameter<Integer> ENERGY = EntityDataManager.createKey(EntityEnchantedBroom.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(EntityEnchantedBroom.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.createKey(EntityEnchantedBroom.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> FORWARD_DIRECTION = EntityDataManager.createKey(EntityEnchantedBroom.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> MODEL_TYPE = EntityDataManager.createKey(EntityEnchantedBroom.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HEAD_TYPE = EntityDataManager.createKey(EntityEnchantedBroom.class, DataSerializers.VARINT);

    public EntityEnchantedBroom(EntityType<EntityEnchantedBroom> entityType, World world) {
        super(entityType, world);
    }

    public EntityEnchantedBroom(World world, double x, double y, double z) {
        super(ModEntities.enchanted_broom, world, x, y, z);
    }

    public EntityEnchantedBroom(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(ModEntities.enchanted_broom, world);
    }

    @Override
    public ItemStack getStack() {
        ItemStack stack = new ItemStack(ModItems.enchantedBroom);
        ItemAbstractFlyingThing.setModelType(stack, getModelType());
        if (hasCustomName()) {
            stack.setDisplayName(getCustomName());
        }
        ItemAbstractFlyingThing.setEnergy(stack, getEnergy());
        if (hasSoulbound()) {
            ItemAbstractFlyingThing.setSoulbound(stack);
        }
        if (getHeadType() > 0) {
            ItemEnchantedBroom.setHeadType(stack, getHeadType());
        }
        return stack;
    }

    @Override
    public boolean canFlyInDimension(DimensionType dimensionType) {
        if (world.isRemote) {
            return true;
        }
        return !ConfigFlyingThings.deniedDimensionToFly.deniedDimensionBroom.get().contains(Helper.getDimensionString(dimensionType));
    }

    @Override
    public void tick() {
        if (ConfigFlyingThings.general.allowSpecialRegen.get() && getEnergy() < ConfigFlyingThings.General.getMaxEnergy() && ticksExisted % 10 == 0 && world.isBlockLoaded(getPosition().down()) && world.getBlockState(getPosition().down()).getBlock() == Blocks.RED_MUSHROOM_BLOCK) {
            if (!world.isRemote) {
                setEnergy(getEnergy() + 4);
                world.playSound(null, getPosition(), SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS, 0.5f, 0.5f);
            } else {
                for (int i = 0; i < Helper.getRandom(10, 45); i++) {
                    world.addParticle(ParticleTypes.WITCH, posX + rand.nextGaussian() * 0.4D, getBoundingBox().maxY + rand.nextGaussian() * 0.12999999523162842D, posZ + rand.nextGaussian() * 0.4D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        super.tick();
    }

    @Override
    public double getMountedYOffset() {
        return 0d;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected boolean onInteractWithPlayerItem(ItemStack stack, PlayerEntity player, Hand hand) {
        if (stack.getItem() instanceof DyeItem) {
            setModelType(((DyeItem) stack.getItem()).getDyeColor().getId());
            if (!player.abilities.isCreativeMode) {
                stack.shrink(1);
            }
            return true;
        } else if (stack.getItem() == Item.getItemFromBlock(Blocks.WET_SPONGE)) {
            setModelType(12);
            return true;
        } else if (stack.getItem() == Items.NAME_TAG && stack.hasDisplayName()) {
            setCustomName(stack.getDisplayName());
            if (!player.abilities.isCreativeMode) {
                stack.shrink(1);
            }
            return true;
        } else if (Helper.isDateAroundHalloween()) {
            if (stack.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN)) {
                setHeadType(1);
                if (!player.abilities.isCreativeMode) {
                    stack.shrink(1);
                }
                return true;
            } else if (stack.getItem() == Items.SKELETON_SKULL) {
                setHeadType(2);
                if (!player.abilities.isCreativeMode) {
                    stack.shrink(1);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected DataParameter<Integer> getDataEnergy() {
        return ENERGY;
    }

    @Override
    public DataParameter<Float> getDataDamageTaken() {
        return DAMAGE_TAKEN;
    }

    @Override
    protected DataParameter<Integer> getDataTimeSinceHit() {
        return TIME_SINCE_HIT;
    }

    @Override
    protected DataParameter<Integer> getDataForwardDirection() {
        return FORWARD_DIRECTION;
    }

    @Override
    protected DataParameter<Integer> getDataModelType() {
        return MODEL_TYPE;
    }

    public int getHeadType() {
        return dataManager.get(HEAD_TYPE);
    }

    public void setHeadType(int headType) {
        dataManager.set(HEAD_TYPE, headType);
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(HEAD_TYPE, 0);
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        tag.putInt("head_type", getHeadType());
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        if (tag.contains("head_type", Constants.NBT.TAG_INT)) {
            setHeadType(tag.getInt("head_type"));
        }
    }
}
