package ovh.corail.flying_things.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemHandlerHelper;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.helper.TimeHelper;

import javax.annotation.Nullable;
import java.util.List;

public abstract class EntityAbstractFlyingThing extends Entity {
    private boolean hasSoulbound = false;
    private boolean wasOnGround = false;
    private double speedFactor = 0.01d; // ConfigFlyingThings.general.getAccelerationIncrement() / 100d;
    public double speed;
    private double lerpX, lerpY, lerpZ, lerpYaw, lerpPitch;
    private int lerpSteps;
    private double velocityX, velocityY, velocityZ;

    public EntityAbstractFlyingThing(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.blocksBuilding = true;
    }

    public EntityAbstractFlyingThing(EntityType<?> entityType, World world, double x, double y, double z) {
        this(entityType, world);
        setPos(x, y, z);
        setDeltaMovement(Vector3d.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected abstract DataParameter<Integer> getDataEnergy();

    protected abstract DataParameter<Float> getDataDamageTaken();

    protected abstract DataParameter<Integer> getDataTimeSinceHit();

    protected abstract DataParameter<Integer> getDataForwardDirection();

    protected abstract DataParameter<Integer> getDataModelType();

    public abstract ItemStack getStack();

    public abstract boolean canFlyInDimension(RegistryKey<World> dimensionType);

    protected abstract boolean onInteractWithPlayerItem(ItemStack stack, PlayerEntity player, Hand hand);

    @Override
    protected void defineSynchedData() {
        this.entityData.define(getDataTimeSinceHit(), 0);
        this.entityData.define(getDataForwardDirection(), 1);
        this.entityData.define(getDataEnergy(), ConfigFlyingThings.shared_datas.maxEnergy.get());
        this.entityData.define(getDataDamageTaken(), 0f);
        this.entityData.define(getDataModelType(), 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        tag.putInt("model_type", getModelType());
        tag.putInt("energy", getEnergy());
        tag.putFloat("damage_taken", getDamageTaken());
        tag.putBoolean("soulbound", this.hasSoulbound);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        if (tag.contains("model_type", Constants.NBT.TAG_INT)) {
            setModelType(tag.getInt("model_type"));
        }
        if (tag.contains("energy", Constants.NBT.TAG_INT)) {
            setEnergy(tag.getInt("energy"));
        }
        if (tag.contains("damage_taken", Constants.NBT.TAG_FLOAT)) {
            setDamageTaken(tag.getFloat("damage_taken"));
        }
        if (tag.contains("soulbound", Constants.NBT.TAG_BYTE)) {
            setSoulbound(tag.getBoolean("soulbound"));
        }
    }

    public boolean hasSoulbound() {
        return this.hasSoulbound;
    }

    public void setSoulbound(boolean flag) {
        this.hasSoulbound = flag;
    }

    public void setEnergy(int energy) {
        this.entityData.set(getDataEnergy(), MathHelper.clamp(energy, 0, ConfigFlyingThings.shared_datas.maxEnergy.get()));
    }

    public int getEnergy() {
        return MathHelper.clamp(this.entityData.get(getDataEnergy()), 0, ConfigFlyingThings.shared_datas.maxEnergy.get());
    }

    public void setDamageTaken(float damage) {
        this.entityData.set(getDataDamageTaken(), damage);
    }

    public float getDamageTaken() {
        return this.entityData.get(getDataDamageTaken());
    }

    public void setTimeSinceHit(int time) {
        this.entityData.set(getDataTimeSinceHit(), time);
    }

    public int getTimeSinceHit() {
        return this.entityData.get(getDataTimeSinceHit());
    }

    public void setForwardDirection(int direction) {
        this.entityData.set(getDataForwardDirection(), direction);
    }

    public int getForwardDirection() {
        return this.entityData.get(getDataForwardDirection());
    }

    public void setModelType(int modelType) {
        if (modelType >= 0) {
            this.entityData.set(getDataModelType(), modelType);
        }
    }

    public int getModelType() {
        return this.entityData.get(getDataModelType());
    }

    @Override
    public ActionResultType interact(PlayerEntity player, Hand hand) {
        if (this.level.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty() && !isVehicle() && !player.isCrouching() && onInteractWithPlayerItem(stack, player, hand)) {
            return ActionResultType.SUCCESS;
        }
        if (player.isCrouching()) {
            ItemHandlerHelper.giveItemToPlayer(player, getStack());
            remove();
            return ActionResultType.SUCCESS;
        } else {
            assert player.level.getServer() != null;
            if (player.level.getServer().isFlightAllowed()) {
                if (canFlyInDimension(this.level.dimension())) {
                    return player.startRiding(this) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
                } else {
                    player.sendMessage(new TranslationTextComponent("flying_things.message.denied_dimension_to_fly", getDisplayName()), Util.NIL_UUID);
                    return ActionResultType.FAIL;
                }
            } else if (hand == Hand.MAIN_HAND) {
                player.sendMessage(new TranslationTextComponent("flying_things.message.flight_not_allowed"), Util.NIL_UUID);
                return ActionResultType.FAIL;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public boolean canBeRiddenInWater(Entity rider) {
        return ConfigFlyingThings.general.allowToFlyInWater.get();
    }

    @Override
    protected void removePassenger(Entity passenger) {
        boolean valid = !this.level.isClientSide && passenger instanceof PlayerEntity && getControllingPassenger() == passenger && passenger.getVehicle() != this;
        super.removePassenger(passenger);
        if (valid && isAlive()) {
            ItemHandlerHelper.giveItemToPlayer((PlayerEntity) passenger, getStack());
            remove();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (getControllingPassenger() != null) {
                if (getEnergy() > 0 && TimeHelper.atInterval(this.tickCount, TimeHelper.tickFromSecond(ConfigFlyingThings.general.timeToLoseEnergy.get()))) {
                    setEnergy(getEnergy() - 1);
                }
            } else {
                if (getEnergy() < ConfigFlyingThings.shared_datas.maxEnergy.get() && TimeHelper.atInterval(this.tickCount, TimeHelper.tickFromSecond(ConfigFlyingThings.general.timeToRecoverEnergy.get()))) {
                    setEnergy(getEnergy() + 1);
                }
            }
        }
        if (getTimeSinceHit() > 0) {
            setTimeSinceHit(getTimeSinceHit() - 1);
        }
        if (getDamageTaken() > 0f) {
            setDamageTaken(getDamageTaken() - 1f);
        }
        this.xo = getX();
        this.yo = getY();
        this.zo = getZ();
        final double speedMax = (getEnergy() > 0 ? ConfigFlyingThings.shared_datas.speedMax.get() : ConfigFlyingThings.shared_datas.speedMaxNoEnergy.get()) / 100d;
        if (this.speed > speedMax) {
            double reducedMotion = speedMax / this.speed;
            setDeltaMovement(getDeltaMovement().multiply(reducedMotion, reducedMotion, reducedMotion));
            this.speed = speedMax;
        }
        double initialSpeed = getDeltaMovement().length();
        double newPosX, newPosY, newPosZ;
        if (this.level.isClientSide && !isVehicle()) {
            if (this.lerpSteps > 0) {
                newPosX = getX() + (this.lerpX - getX()) / (double) this.lerpSteps;
                newPosY = getY() + (this.lerpY - getY()) / (double) this.lerpSteps;
                newPosZ = getZ() + (this.lerpZ - getZ()) / (double) this.lerpSteps;
                double d10 = MathHelper.wrapDegrees(this.lerpYaw - (double) this.yRot);
                this.yRot = (float) ((double) this.yRot + d10 / (double) this.lerpSteps);
                this.xRot = (float) ((double) this.xRot + (this.lerpPitch - (double) this.xRot) / (double) this.lerpSteps);
                --this.lerpSteps;
                absMoveTo(newPosX, newPosY, newPosZ, this.yRot, this.xRot);
            } else {
                newPosX = getX() + getDeltaMovement().x;
                newPosY = getY() + getDeltaMovement().y;
                newPosZ = getZ() + getDeltaMovement().z;
                setPos(newPosX, newPosY, newPosZ);
                setRot((float) ((double) this.yRot + (this.lerpYaw - (double) this.yRot)), (float) ((double) this.xRot + (this.lerpPitch - (double) this.xRot)));
                setDeltaMovement(getDeltaMovement().multiply(0.9900000095367432d, 1d, 0.9900000095367432d));
            }
        } else {
            final double accelerationIncrement = ConfigFlyingThings.shared_datas.accelerationIncrement.get() / 100d;
            if (getControllingPassenger() instanceof LivingEntity) {
                if (((LivingEntity) getControllingPassenger()).zza < 0d) {
                    setDeltaMovement(getDeltaMovement().multiply(1d - accelerationIncrement, 1d, 1d - accelerationIncrement));
                } else if (((LivingEntity) getControllingPassenger()).zza > 0d) {
                    double dirX = -Math.sin(getControllingPassenger().yRot * Math.PI / 180f);
                    double dirZ = Math.cos(getControllingPassenger().yRot * Math.PI / 180f);
                    double dirY = -Math.sin(getControllingPassenger().xRot * Math.PI / 180f);
                    if (dirY > -0.5d && dirY < 0.2d) {
                        dirY = 0d;
                    } else if (dirY < 0d) {
                        dirY *= 0.5d;
                    }
                    setDeltaMovement(new Vector3d(getDeltaMovement().x + dirX * this.speedFactor * 0.1d, dirY * this.speedFactor * 2d, getDeltaMovement().z + dirZ * this.speedFactor * 0.1d));
                }
            } else if (!isVehicle()) {
                double moX = getDeltaMovement().x * 0.9d;
                double moZ = getDeltaMovement().z * 0.9d;
                setDeltaMovement(new Vector3d(Math.abs(moX) < 0.01d ? 0d : moX, getDeltaMovement().y - (!this.onGround ? 0.2d : 0d), Math.abs(moZ) < 0.01d ? 0d : moZ));
            }

            this.speed = getDeltaMovement().length();
            // about the acceleration
            final double accelerationMax = ConfigFlyingThings.shared_datas.accelerationMax.get() / 100d;
            if (this.speed > initialSpeed && this.speedFactor < accelerationMax) {
                this.speedFactor += (accelerationMax - this.speedFactor) / ConfigFlyingThings.shared_datas.accelerationMax.get();
                if (this.speedFactor > accelerationMax) {
                    this.speedFactor = accelerationMax;
                }
            } else if (this.speed < initialSpeed) {
                this.speedFactor -= (this.speedFactor - accelerationIncrement) / ConfigFlyingThings.shared_datas.accelerationMax.get();
                if (this.speedFactor < accelerationIncrement) {
                    this.speedFactor = accelerationIncrement;
                }
            }
            // move
            move(MoverType.SELF, getDeltaMovement());
            double reducedMotion = !this.wasOnGround && this.onGround ? 0.5d : 0.9900000095367432d;
            this.wasOnGround = this.onGround;
            setDeltaMovement(getDeltaMovement().multiply(reducedMotion, reducedMotion, reducedMotion));
            // about rotations
            this.xRot = 0f;
            double rotYaw = this.yRot;
            double x = this.xo - getX();
            double z = this.zo - getZ();
            if (x * x + z * z > 0.001d) {
                rotYaw = (float) (Math.atan2(z, x) * 180d / Math.PI) + 90f;
            }
            double adjust = MathHelper.wrapDegrees(rotYaw - (double) this.yRot);
            this.yRot = this.yRot + (float) adjust;
            setRot(this.yRot, this.xRot);
            if (!this.level.isClientSide) {
                List<Entity> list = this.level.getEntities(this, getBoundingBox().inflate(0.20000000298023224d, -0.009999999776482582d, 0.20000000298023224d), p -> !p.isAlliedTo(this));
                if (!list.isEmpty()) {
                    for (Entity entity : list) {
                        if (!entity.hasPassenger(this)) {
                            if (canAddPassenger(entity) && !entity.isPassenger() && entity.getBbWidth() < this.getBbWidth() && entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
                                entity.startRiding(this);
                            } else if (entity.isPushable()) {
                                push(entity);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void checkInsideBlocks() {
        AxisAlignedBB bounds = getBoundingBox();
        boolean isHighSpeed = this.speed > 0.4d;
        if (isHighSpeed) {
            bounds = bounds.inflate(1d, 0.3d, 1d);
        }
        BlockPos.Mutable pos0 = new BlockPos.Mutable(bounds.minX + 0.001d, bounds.minY + 0.001d, bounds.minZ + 0.001d);
        BlockPos.Mutable pos1 = new BlockPos.Mutable(bounds.maxX - 0.001d, bounds.maxY - 0.001d, bounds.maxZ - 0.001d);
        BlockPos.Mutable pos2 = new BlockPos.Mutable();
        if (this.level.hasChunksAt(pos0, pos1)) {
            for (int i = pos0.getX(); i <= pos1.getX(); ++i) {
                for (int j = pos0.getY(); j <= pos1.getY(); ++j) {
                    for (int k = pos0.getZ(); k <= pos1.getZ(); ++k) {
                        pos2.set(i, j, k);
                        BlockState state = this.level.getBlockState(pos2);
                        try {
                            if (!this.level.isClientSide && state.getMaterial() != Material.AIR && state.getBlock() != Blocks.SOUL_SAND) {
                                if (ConfigFlyingThings.general.allowToBreakPlant.get() && isHighSpeed && !state.getMaterial().isLiquid() && (state.getMaterial().isReplaceable() || state.getMaterial() == Material.PLANT || state.getMaterial() == Material.LEAVES)) {
                                    this.level.destroyBlock(pos2, true);
                                } else {
                                    state.getBlock().entityInside(state, this.level, pos2, this);
                                }
                            }
                            onInsideBlock(state);
                        } catch (Throwable throwable) {
                            CrashReport crashreport = CrashReport.forThrowable(throwable, "Colliding entity with block");
                            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being collided with");
                            CrashReportCategory.populateBlockDetails(crashreportcategory, pos2, state);
                            throw new ReportedException(crashreport);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return canFlyInDimension(this.level.dimension()) && super.canAddPassenger(passenger) && passenger instanceof PlayerEntity;
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void animateHurt() {
        setForwardDirection(-getForwardDirection());
        setTimeSinceHit(10);
        setDamageTaken(getDamageTaken() * 11f);
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void lerpMotion(double x, double y, double z) {
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        setDeltaMovement(this.velocityX, this.velocityY, this.velocityZ);
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = (double) yaw;
        this.lerpPitch = (double) pitch;
        setDeltaMovement(this.velocityX, this.velocityY, this.velocityZ);
        if (!isVehicle()) {
            this.lerpSteps = posRotationIncrements + 5;
        } else {
            double d3 = x - getX();
            double d4 = y - getY();
            double d5 = z - getZ();
            double d6 = d3 * d3 + d4 * d4 + d5 * d5;
            if (d6 <= 1d) {
                return;
            }
            this.lerpSteps = 3;
        }
    }

    @Override
    public void setSecondsOnFire(int amount) {
    }

    @Override
    public boolean isMovementNoisy() {
        return false;
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        List<Entity> list = getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean isPickable() {
        return isAlive() && !isVehicle();
    }

    @Override
    public void push(Entity entity) {
        if (Helper.isFlyingthing(entity)) {
            if (entity.getBoundingBox().minY < getBoundingBox().maxY) {
                super.push(entity);
            }
        } else if (entity.getBoundingBox().minY <= getBoundingBox().minY) {
            super.push(entity);
        }
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public double getPassengersRidingOffset() {
        return (double) this.getBbHeight() * 0.5d;
    }

    @Override
    public double getMyRidingOffset() {
        return 0d;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        }
        if (!this.level.isClientSide && isAlive()) {
            if (source instanceof IndirectEntityDamageSource && source.getEntity() != null && hasPassenger(source.getEntity())) {
                return false;
            }
            setForwardDirection(-getForwardDirection());
            setTimeSinceHit(10);
            setDamageTaken(getDamageTaken() + amount * 10f);
            markHurt();
            boolean creativeDamage = source.getEntity() instanceof PlayerEntity && ((PlayerEntity) source.getEntity()).abilities.instabuild;
            if (creativeDamage || getDamageTaken() > 40f) {
                if (Helper.isValidPlayer(getControllingPassenger())) {
                    ItemHandlerHelper.giveItemToPlayer((PlayerEntity) getControllingPassenger(), getStack());
                } else {
                    spawnAtLocation(getStack(), 0f);
                }
                remove();
            }
        }
        return true;
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            setPos(this.lerpX, this.lerpY, this.lerpZ);
            this.yRot = (float) this.lerpYaw;
            this.xRot = (float) this.lerpPitch;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void onPassengerTurned(Entity entityToUpdate) {
        float f = MathHelper.wrapDegrees(entityToUpdate.yRot - this.yRot);
        float f1 = MathHelper.clamp(f, -105f, 105f);
        entityToUpdate.yRotO += f1 - f;
        entityToUpdate.yRot += f1 - f;

        entityToUpdate.setYBodyRot(this.yRot);
        //entityToUpdate.setRotationYawHead(this.rotationYaw);
    }

    @Override
    public ITextComponent getName() {
        if (!this.level.isClientSide) {
            Entity owner = getControllingPassenger();
            if (owner != null) {
                return owner.getName();
            }
        }
        return super.getName();
    }
}
