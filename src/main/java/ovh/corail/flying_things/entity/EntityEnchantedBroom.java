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
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.FMLPlayMessages;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.helper.TimeHelper;
import ovh.corail.flying_things.item.ItemAbstractFlyingThing;
import ovh.corail.flying_things.item.ItemEnchantedBroom;
import ovh.corail.flying_things.registry.ModEntities;
import ovh.corail.flying_things.registry.ModItems;

public class EntityEnchantedBroom extends EntityAbstractFlyingThing {
    private static final DataParameter<Integer> ENERGY = EntityDataManager.defineId(EntityEnchantedBroom.class, DataSerializers.INT);
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.defineId(EntityEnchantedBroom.class, DataSerializers.INT);
    private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.defineId(EntityEnchantedBroom.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> FORWARD_DIRECTION = EntityDataManager.defineId(EntityEnchantedBroom.class, DataSerializers.INT);
    private static final DataParameter<Integer> MODEL_TYPE = EntityDataManager.defineId(EntityEnchantedBroom.class, DataSerializers.INT);
    private static final DataParameter<Integer> HEAD_TYPE = EntityDataManager.defineId(EntityEnchantedBroom.class, DataSerializers.INT);

    public EntityEnchantedBroom(EntityType<EntityEnchantedBroom> entityType, World world) {
        super(entityType, world);
    }

    public EntityEnchantedBroom(World world, double x, double y, double z) {
        super(ModEntities.enchanted_broom.get(), world, x, y, z);
    }

    public EntityEnchantedBroom(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(ModEntities.enchanted_broom.get(), world);
    }

    @Override
    public ItemStack getStack() {
        ItemStack stack = new ItemStack(ModItems.enchantedBroom.get());
        ItemAbstractFlyingThing.setModelType(stack, getModelType());
        if (hasCustomName()) {
            stack.setHoverName(getCustomName());
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
    public boolean canFlyInDimension(RegistryKey<World> dimensionType) {
        if (level.isClientSide) {
            return true;
        }
        return !ConfigFlyingThings.deniedDimensionToFly.deniedDimensionBroom.get().contains(Helper.getDimensionString(dimensionType));
    }

    @Override
    public void tick() {
        if (ConfigFlyingThings.general.allowSpecialRegen.get() && getEnergy() < ConfigFlyingThings.shared_datas.maxEnergy.get() && TimeHelper.atInterval(this.tickCount, 10) && this.level.hasChunkAt(blockPosition().below()) && this.level.getBlockState(blockPosition().below()).getBlock() == Blocks.RED_MUSHROOM_BLOCK) {
            if (!this.level.isClientSide) {
                setEnergy(getEnergy() + 4);
                this.level.playSound(null, blockPosition(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS, 0.5f, 0.5f);
            } else {
                for (int i = 0; i < Helper.getRandom(10, 45); i++) {
                    this.level.addParticle(ParticleTypes.WITCH, getX() + this.random.nextGaussian() * 0.4d, getBoundingBox().maxY + this.random.nextGaussian() * 0.12999999523162842d, getZ() + this.random.nextGaussian() * 0.4d, 0d, 0d, 0d);
                }
            }
        }
        super.tick();
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0d;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected boolean onInteractWithPlayerItem(ItemStack stack, PlayerEntity player, Hand hand) {
        if (stack.getItem() instanceof DyeItem) {
            setModelType(((DyeItem) stack.getItem()).getDyeColor().getId());
            if (!player.abilities.instabuild) {
                stack.shrink(1);
            }
            return true;
        } else if (stack.getItem() == Item.byBlock(Blocks.WET_SPONGE)) {
            setModelType(12);
            return true;
        } else if (stack.getItem() == Items.NAME_TAG && stack.hasCustomHoverName()) {
            setCustomName(stack.getHoverName());
            if (!player.abilities.instabuild) {
                stack.shrink(1);
            }
            return true;
        } else if (Helper.isDateAroundHalloween()) {
            if (stack.getItem() == Item.byBlock(Blocks.PUMPKIN)) {
                setHeadType(1);
                if (!player.abilities.instabuild) {
                    stack.shrink(1);
                }
                return true;
            } else if (stack.getItem() == Items.SKELETON_SKULL) {
                setHeadType(2);
                if (!player.abilities.instabuild) {
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
        return this.entityData.get(HEAD_TYPE);
    }

    public void setHeadType(int headType) {
        this.entityData.set(HEAD_TYPE, headType);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEAD_TYPE, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("head_type", getHeadType());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("head_type", Constants.NBT.TAG_INT)) {
            setHeadType(tag.getInt("head_type"));
        }
    }
}
