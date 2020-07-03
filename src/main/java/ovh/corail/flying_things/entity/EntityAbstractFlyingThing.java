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
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemHandlerHelper;
import ovh.corail.flying_things.ConfigFlyingThings;
import ovh.corail.flying_things.helper.Helper;

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
        this.preventEntitySpawning = true;
    }

    public EntityAbstractFlyingThing(EntityType<?> entityType, World world, double x, double y, double z) {
        this(entityType, world);
        setPosition(x, y, z);
        setMotion(Vec3d.ZERO);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected abstract DataParameter<Integer> getDataEnergy();

    protected abstract DataParameter<Float> getDataDamageTaken();

    protected abstract DataParameter<Integer> getDataTimeSinceHit();

    protected abstract DataParameter<Integer> getDataForwardDirection();

    protected abstract DataParameter<Integer> getDataModelType();

    public abstract ItemStack getStack();

    public abstract boolean canFlyInDimension(DimensionType dimensionType);

    protected abstract boolean onInteractWithPlayerItem(ItemStack stack, PlayerEntity player, Hand hand);

    @Override
    protected void registerData() {
        this.dataManager.register(getDataTimeSinceHit(), 0);
        this.dataManager.register(getDataForwardDirection(), 1);
        this.dataManager.register(getDataEnergy(), ConfigFlyingThings.General.getMaxEnergy());
        this.dataManager.register(getDataDamageTaken(), 0f);
        this.dataManager.register(getDataModelType(), 0);
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        tag.putInt("model_type", getModelType());
        tag.putInt("energy", getEnergy());
        tag.putFloat("damage_taken", getDamageTaken());
        tag.putBoolean("soulbound", this.hasSoulbound);
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
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
        this.dataManager.set(getDataEnergy(), MathHelper.clamp(energy, 0, ConfigFlyingThings.General.getMaxEnergy()));
    }

    public int getEnergy() {
        return MathHelper.clamp(this.dataManager.get(getDataEnergy()), 0, ConfigFlyingThings.General.getMaxEnergy());
    }

    public void setDamageTaken(float damage) {
        this.dataManager.set(getDataDamageTaken(), damage);
    }

    public float getDamageTaken() {
        return this.dataManager.get(getDataDamageTaken());
    }

    public void setTimeSinceHit(int time) {
        this.dataManager.set(getDataTimeSinceHit(), time);
    }

    public int getTimeSinceHit() {
        return this.dataManager.get(getDataTimeSinceHit());
    }

    public void setForwardDirection(int direction) {
        this.dataManager.set(getDataForwardDirection(), direction);
    }

    public int getForwardDirection() {
        return this.dataManager.get(getDataForwardDirection());
    }

    public void setModelType(int modelType) {
        if (modelType >= 0) {
            this.dataManager.set(getDataModelType(), modelType);
        }
    }

    public int getModelType() {
        return this.dataManager.get(getDataModelType());
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand) {
        if (this.world.isRemote) {
            return true;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && !isBeingRidden() && !player.isSneaking() && onInteractWithPlayerItem(stack, player, hand)) {
            return true;
        }
        if (player.isSneaking()) {
            ItemHandlerHelper.giveItemToPlayer(player, getStack());
            remove();
            return true;
        } else {
            assert player.world.getServer() != null;
            if (player.world.getServer().isFlightAllowed()) {
                if (canFlyInDimension(this.world.dimension.getType())) {
                    return player.startRiding(this);
                } else {
                    player.sendMessage(new TranslationTextComponent("flying_things.message.denied_dimension_to_fly", getDisplayName()));
                    return false;
                }
            } else if (hand == Hand.MAIN_HAND) {
                player.sendMessage(new TranslationTextComponent("flying_things.message.flight_not_allowed"));
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean canBeRiddenInWater(Entity rider) {
        return ConfigFlyingThings.general.allowToFlyInWater.get();
    }

    @Override
    protected void removePassenger(Entity passenger) {
        boolean valid = !this.world.isRemote && passenger instanceof PlayerEntity && getControllingPassenger() == passenger && passenger.getRidingEntity() != this;
        super.removePassenger(passenger);
        if (valid && isAlive()) {
            ItemHandlerHelper.giveItemToPlayer((PlayerEntity) passenger, getStack());
            remove();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isRemote) {
            if (getControllingPassenger() != null) {
                if (getEnergy() > 0 && this.ticksExisted % (ConfigFlyingThings.general.timeToLoseEnergy.get() * 20) == 0) {
                    setEnergy(getEnergy() - 1);
                }
            } else {
                if (getEnergy() < ConfigFlyingThings.General.getMaxEnergy() && this.ticksExisted % (ConfigFlyingThings.general.timeToRecoverEnergy.get() * 20) == 0) {
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
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        final double speedMax = (getEnergy() > 0 ? ConfigFlyingThings.General.getSpeedMax() : ConfigFlyingThings.General.getSpeedMaxNoEnergy()) / 100d;
        if (this.speed > speedMax) {
            double reducedMotion = speedMax / this.speed;
            setMotion(getMotion().mul(reducedMotion, reducedMotion, reducedMotion));
            this.speed = speedMax;
        }
        double initialSpeed = getMotion().length();
        double newPosX, newPosY, newPosZ;
        if (this.world.isRemote && !isBeingRidden()) {
            if (this.lerpSteps > 0) {
                newPosX = this.posX + (this.lerpX - this.posX) / (double) this.lerpSteps;
                newPosY = this.posY + (this.lerpY - this.posY) / (double) this.lerpSteps;
                newPosZ = this.posZ + (this.lerpZ - this.posZ) / (double) this.lerpSteps;
                double d10 = MathHelper.wrapDegrees(this.lerpYaw - (double) this.rotationYaw);
                this.rotationYaw = (float) ((double) this.rotationYaw + d10 / (double) this.lerpSteps);
                this.rotationPitch = (float) ((double) this.rotationPitch + (this.lerpPitch - (double) this.rotationPitch) / (double) this.lerpSteps);
                --this.lerpSteps;
                setPositionAndRotation(newPosX, newPosY, newPosZ, this.rotationYaw, this.rotationPitch);
            } else {
                newPosX = this.posX + getMotion().x;
                newPosY = this.posY + getMotion().y;
                newPosZ = this.posZ + getMotion().z;
                setPosition(newPosX, newPosY, newPosZ);
                setRotation((float) ((double) this.rotationYaw + (this.lerpYaw - (double) this.rotationYaw)), (float) ((double) this.rotationPitch + (this.lerpPitch - (double) this.rotationPitch)));
                setMotion(getMotion().mul(0.9900000095367432d, 1d, 0.9900000095367432d));
            }
        } else {
            final double accelerationIncrement = ConfigFlyingThings.General.getAccelerationIncrement() / 100d;
            if (getControllingPassenger() instanceof LivingEntity) {
                if (((LivingEntity) getControllingPassenger()).moveForward < 0d) {
                    setMotion(getMotion().mul(1d - accelerationIncrement, 1d, 1d - accelerationIncrement));
                } else if (((LivingEntity) getControllingPassenger()).moveForward > 0d) {
                    double dirX = -Math.sin(getControllingPassenger().rotationYaw * Math.PI / 180f);
                    double dirZ = Math.cos(getControllingPassenger().rotationYaw * Math.PI / 180f);
                    double dirY = -Math.sin(getControllingPassenger().rotationPitch * Math.PI / 180f);
                    if (dirY > -0.5d && dirY < 0.2d) {
                        dirY = 0d;
                    } else if (dirY < 0d) {
                        dirY *= 0.5d;
                    }
                    setMotion(new Vec3d(getMotion().x + dirX * this.speedFactor * 0.1d, dirY * this.speedFactor * 2d, getMotion().z + dirZ * this.speedFactor * 0.1d));
                }
            } else if (!isBeingRidden()) {
                double moX = getMotion().x * 0.9d;
                double moZ = getMotion().z * 0.9d;
                setMotion(new Vec3d(Math.abs(moX) < 0.01d ? 0d : moX, getMotion().y - (!this.onGround ? 0.2d : 0d), Math.abs(moZ) < 0.01d ? 0d : moZ));
            }

            this.speed = getMotion().length();
            // about the acceleration
            final double accelerationMax = ConfigFlyingThings.General.getAccelerationMax() / 100d;
            if (this.speed > initialSpeed && this.speedFactor < accelerationMax) {
                this.speedFactor += (accelerationMax - this.speedFactor) / ConfigFlyingThings.General.getAccelerationMax();
                if (this.speedFactor > accelerationMax) {
                    this.speedFactor = accelerationMax;
                }
            } else if (this.speed < initialSpeed) {
                this.speedFactor -= (this.speedFactor - accelerationIncrement) / ConfigFlyingThings.General.getAccelerationMax();
                if (this.speedFactor < accelerationIncrement) {
                    this.speedFactor = accelerationIncrement;
                }
            }
            // move
            move(MoverType.SELF, getMotion());
            double reducedMotion = !this.wasOnGround && this.onGround ? 0.5d : 0.9900000095367432d;
            this.wasOnGround = this.onGround;
            setMotion(getMotion().mul(reducedMotion, reducedMotion, reducedMotion));
            // about rotations
            this.rotationPitch = 0f;
            double rotYaw = this.rotationYaw;
            double x = this.prevPosX - this.posX;
            double z = this.prevPosZ - this.posZ;
            if (x * x + z * z > 0.001d) {
                rotYaw = (float) (Math.atan2(z, x) * 180d / Math.PI) + 90f;
            }
            double adjust = MathHelper.wrapDegrees(rotYaw - (double) this.rotationYaw);
            this.rotationYaw = this.rotationYaw + (float) adjust;
            setRotation(this.rotationYaw, this.rotationPitch);
            if (!this.world.isRemote) {
                List<Entity> list = this.world.getEntitiesInAABBexcluding(this, getBoundingBox().grow(0.20000000298023224d, -0.009999999776482582d, 0.20000000298023224d), p -> !p.isOnSameTeam(this));
                if (!list.isEmpty()) {
                    for (Entity entity : list) {
                        if (!entity.isPassenger(this)) {
                            if (canFitPassenger(entity) && !entity.isPassenger() && entity.getWidth() < this.getWidth() && entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
                                entity.startRiding(this);
                            } else if (entity.canBePushed()) {
                                applyEntityCollision(entity);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void doBlockCollisions() {
        AxisAlignedBB bounds = getBoundingBox();
        boolean isHighSpeed = this.speed > 0.4d;
        if (isHighSpeed) {
            bounds = bounds.grow(1d, 0.3d, 1d);
        }
        PooledMutableBlockPos pos0 = PooledMutableBlockPos.retain(bounds.minX + 0.001d, bounds.minY + 0.001d, bounds.minZ + 0.001d);
        PooledMutableBlockPos pos1 = PooledMutableBlockPos.retain(bounds.maxX - 0.001d, bounds.maxY - 0.001d, bounds.maxZ - 0.001d);
        PooledMutableBlockPos pos2 = PooledMutableBlockPos.retain();
        if (this.world.isAreaLoaded(pos0, pos1)) {
            for (int i = pos0.getX(); i <= pos1.getX(); ++i) {
                for (int j = pos0.getY(); j <= pos1.getY(); ++j) {
                    for (int k = pos0.getZ(); k <= pos1.getZ(); ++k) {
                        pos2.setPos(i, j, k);
                        BlockState state = this.world.getBlockState(pos2);
                        try {
                            if (!this.world.isRemote && state.getMaterial() != Material.AIR && state.getBlock() != Blocks.SOUL_SAND) {
                                if (ConfigFlyingThings.general.allowToBreakPlant.get() && isHighSpeed && !state.getMaterial().isLiquid() && (state.getMaterial().isReplaceable() || state.getMaterial() == Material.PLANTS || state.getMaterial() == Material.LEAVES)) {
                                    this.world.destroyBlock(pos2, true);
                                } else {
                                    state.getBlock().onEntityCollision(state, this.world, pos2, this);
                                }
                            }
                            onInsideBlock(state);
                        } catch (Throwable throwable) {
                            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Colliding entity with block");
                            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being collided with");
                            CrashReportCategory.addBlockInfo(crashreportcategory, pos2, state);
                            throw new ReportedException(crashreport);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return canFlyInDimension(this.world.dimension.getType()) && super.canFitPassenger(passenger) && passenger instanceof PlayerEntity;
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void performHurtAnimation() {
        setForwardDirection(-getForwardDirection());
        setTimeSinceHit(10);
        setDamageTaken(getDamageTaken() * 11f);
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void setVelocity(double x, double y, double z) {
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        setMotion(this.velocityX, this.velocityY, this.velocityZ);
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = (double) yaw;
        this.lerpPitch = (double) pitch;
        setMotion(this.velocityX, this.velocityY, this.velocityZ);
        if (!isBeingRidden()) {
            this.lerpSteps = posRotationIncrements + 5;
        } else {
            double d3 = x - this.posX;
            double d4 = y - this.posY;
            double d5 = z - this.posZ;
            double d6 = d3 * d3 + d4 * d4 + d5 * d5;
            if (d6 <= 1d) {
                return;
            }
            this.lerpSteps = 3;
        }
    }

    @Override
    protected void dealFireDamage(int amount) {
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        List<Entity> list = getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean canBeCollidedWith() {
        return isAlive() && !isBeingRidden();
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (Helper.isFlyingthing(entity)) {
            if (entity.getBoundingBox().minY < getBoundingBox().maxY) {
                super.applyEntityCollision(entity);
            }
        } else if (entity.getBoundingBox().minY <= getBoundingBox().minY) {
            super.applyEntityCollision(entity);
        }
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public double getMountedYOffset() {
        return (double) this.getHeight() * 0.5d;
    }

    @Override
    public double getYOffset() {
        return 0d;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        }
        if (!this.world.isRemote && isAlive()) {
            if (source instanceof IndirectEntityDamageSource && source.getTrueSource() != null && isPassenger(source.getTrueSource())) {
                return false;
            }
            setForwardDirection(-getForwardDirection());
            setTimeSinceHit(10);
            setDamageTaken(getDamageTaken() + amount * 10f);
            markVelocityChanged();
            boolean creativeDamage = source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity) source.getTrueSource()).abilities.isCreativeMode;
            if (creativeDamage || getDamageTaken() > 40f) {
                if (Helper.isValidPlayer(getControllingPassenger())) {
                    ItemHandlerHelper.giveItemToPlayer((PlayerEntity) getControllingPassenger(), getStack());
                } else {
                    entityDropItem(getStack(), 0f);
                }
                remove();
            }
        }
        return true;
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (canPassengerSteer() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.posX = this.lerpX;
            this.posY = this.lerpY;
            this.posZ = this.lerpZ;
            this.rotationYaw = (float) this.lerpYaw;
            this.rotationPitch = (float) this.lerpPitch;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void applyOrientationToEntity(Entity entityToUpdate) {
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -105f, 105f);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;

        entityToUpdate.setRenderYawOffset(this.rotationYaw);
        //entityToUpdate.setRotationYawHead(this.rotationYaw);
    }

    @Override
    public ITextComponent getName() {
        if (!this.world.isRemote) {
            Entity owner = getControllingPassenger();
            if (owner != null) {
                return owner.getName();
            }
        }
        return super.getName();
    }
}
