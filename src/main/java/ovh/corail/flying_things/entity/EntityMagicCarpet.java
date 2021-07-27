package ovh.corail.flying_things.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.helper.TimeHelper;
import ovh.corail.flying_things.item.ItemAbstractFlyingThing;
import ovh.corail.flying_things.registry.ModEntities;
import ovh.corail.flying_things.registry.ModItems;

import java.util.stream.IntStream;

public class EntityMagicCarpet extends EntityAbstractFlyingThing {
    private static final DataParameter<Integer> ENERGY = EntityDataManager.defineId(EntityMagicCarpet.class, DataSerializers.INT);
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.defineId(EntityMagicCarpet.class, DataSerializers.INT);
    private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.defineId(EntityMagicCarpet.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> FORWARD_DIRECTION = EntityDataManager.defineId(EntityMagicCarpet.class, DataSerializers.INT);
    private static final DataParameter<Integer> MODEL_TYPE = EntityDataManager.defineId(EntityMagicCarpet.class, DataSerializers.INT);

    public EntityMagicCarpet(EntityType<EntityMagicCarpet> entityType, World world) {
        super(entityType, world);
    }

    public EntityMagicCarpet(World world, double x, double y, double z) {
        super(ModEntities.magic_carpet.get(), world, x, y, z);
    }

    public EntityMagicCarpet(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(ModEntities.magic_carpet.get(), world);
    }

    @Override
    public ItemStack getStack() {
        ItemStack stack = new ItemStack(ModItems.magicCarpet.get());
        ItemAbstractFlyingThing.setModelType(stack, getModelType());
        if (hasCustomName()) {
            stack.setHoverName(getCustomName());
        }
        ItemAbstractFlyingThing.setEnergy(stack, getEnergy());
        if (hasSoulbound()) {
            ItemAbstractFlyingThing.setSoulbound(stack);
        }
        return stack;
    }

    @Override
    public boolean canFlyInDimension(RegistryKey<World> dimensionType) {
        if (this.level.isClientSide) {
            return true;
        }
        return !ConfigFlyingThings.deniedDimensionToFly.deniedDimensionCarpet.get().contains(Helper.getDimensionString(dimensionType));
    }

    @Override
    protected boolean onInteractWithPlayerItem(ItemStack stack, PlayerEntity player, Hand hand) {
        if (stack.getItem() == ModItems.pumpkinStick.get()) {
            int res = Helper.getRandom(9, 13);
            if (res == getModelType()) {
                res = (res == 13 ? 9 : res + 1);
            }
            setModelType(res);
            if (!player.abilities.instabuild) {
                stack.shrink(1);
            }
            return true;
        } else if (stack.getItem() == Items.NAME_TAG && stack.hasCustomHoverName()) {
            setCustomName(stack.getHoverName());
            if (!player.abilities.instabuild) {
                stack.shrink(1);
            }
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        if (ConfigFlyingThings.general.allowSpecialRegen.get() && getEnergy() < ConfigFlyingThings.shared_datas.maxEnergy.get() && TimeHelper.atInterval(this.tickCount, 10) && this.level.hasChunkAt(blockPosition().below()) && this.level.getBlockState(blockPosition().below()).getBlock() == Blocks.SOUL_SAND) {
            if (!this.level.isClientSide) {
                setEnergy(getEnergy() + 4);
                this.level.playSound(null, blockPosition(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS, 0.5f, 0.5f);
            } else {
                IntStream.range(0, Helper.getRandom(10, 45)).forEach(i -> this.level.addParticle(ParticleTypes.WITCH, getX() + this.random.nextGaussian() * 0.4D, getBoundingBox().maxY + this.random.nextGaussian() * 0.12999999523162842d, getZ() + this.random.nextGaussian() * 0.4d, 0d, 0d, 0d));
            }
        }
        super.tick();
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.1d;
    }

    @Override
    protected DataParameter<Integer> getDataEnergy() {
        return ENERGY;
    }

    @Override
    protected DataParameter<Float> getDataDamageTaken() {
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
}
