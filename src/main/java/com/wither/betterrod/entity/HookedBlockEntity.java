package com.wither.betterrod.entity;

import com.mojang.logging.LogUtils;
import com.wither.betterrod.init.EntityRegister;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.Predicate;

public class HookedBlockEntity extends Entity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final BlockState DEFAULT_BLOCK_STATE = Blocks.SAND.defaultBlockState();
    private BlockState blockState = DEFAULT_BLOCK_STATE;
    public int time = 0;
    public boolean dropItem = true;
    private boolean cancelDrop = false;
    private boolean hurtEntities;
    private int fallDamageMax = 40;
    private float fallDamagePerDistance = 0.0F;
    public @Nullable CompoundTag blockData;
    public boolean forceTickAfterTeleportToDuplicate;
    private Entity hook;
    protected static final EntityDataAccessor<@NotNull BlockPos> DATA_START_POS = SynchedEntityData.defineId(HookedBlockEntity.class, EntityDataSerializers.BLOCK_POS);

    public HookedBlockEntity(EntityType<? extends @NotNull HookedBlockEntity> type, Level level) {
        super(type, level);
    }

    private HookedBlockEntity(Level level, double x, double y, double z, BlockState blockState, FishingHook hook) {
        this(EntityRegister.HOOKED_BLOCK.get(), level);
        this.blockState = blockState;
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
        this.hook = hook;
    }

    public static HookedBlockEntity beHooked(Level level, BlockPos pos, BlockState state, FishingHook hook) {
        HookedBlockEntity entity = new HookedBlockEntity(
                level,
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                state.hasProperty(BlockStateProperties.WATERLOGGED) ? state.setValue(BlockStateProperties.WATERLOGGED, false) : state,
                hook
        );
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        level.addFreshEntity(entity);
        return entity;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public final boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource source, float damage) {
        if (!this.isInvulnerableToBase(source)) {
            this.markHurt();
        }
        return false;
    }

    public void setStartPos(BlockPos pos) {
        this.entityData.set(DATA_START_POS, pos);
    }

    public BlockPos getStartPos() {
        return this.entityData.get(DATA_START_POS);
    }

    @Override
    protected Entity.@NotNull MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        entityData.define(DATA_START_POS, BlockPos.ZERO);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void tick() {
        if (this.blockState.isAir()) {
            this.discard();
        } else {
            Block block = this.blockState.getBlock();
            this.time++;
            this.applyGravity();
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.applyEffectsFromBlocks();
            this.handlePortal();
            if (this.level() instanceof ServerLevel serverLevel && (this.isAlive() || this.forceTickAfterTeleportToDuplicate)) {
                BlockPos pos = this.blockPosition();
                boolean isConcrete = this.blockState.getBlock() instanceof ConcretePowderBlock;
                boolean isStuckInWater = isConcrete && this.blockState.canBeHydrated(this.level(), pos, this.level().getFluidState(pos), pos);
                boolean isHooked = this.hook == null || !this.hook.isRemoved();
                double moveVec = this.getDeltaMovement().lengthSqr();
                if (isConcrete && moveVec > 1.0) {
                    BlockHitResult clip = this.level()
                            .clip(
                                    new ClipContext(
                                            new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this
                                    )
                            );
                    if (clip.getType() != HitResult.Type.MISS && this.blockState.canBeHydrated(this.level(), pos, this.level().getFluidState(clip.getBlockPos()), clip.getBlockPos())) {
                        pos = clip.getBlockPos();
                        isStuckInWater = true;
                    }
                }

                if (!this.onGround() && !isStuckInWater) {
                    if (this.time > 100 && (pos.getY() <= this.level().getMinY() || pos.getY() > this.level().getMaxY()) || this.time > 600) {
                        if (this.dropItem && serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
                            this.spawnAtLocation(serverLevel, block);
                        }

                        this.discard();
                    }
                } else if(!isHooked){
                    BlockState currentState = this.level().getBlockState(pos);
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
                    if (!currentState.is(Blocks.MOVING_PISTON)) {
                        if (!this.cancelDrop) {
                            boolean mayReplace = currentState.canBeReplaced(
                                    new DirectionalPlaceContext(this.level(), pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP)
                            );
                            boolean wouldContinueFalling = FallingBlock.isFree(this.level().getBlockState(pos.below())) && (!isConcrete || !isStuckInWater);
                            boolean wouldSurvive = (this.blockState.canSurvive(this.level(), pos) && !wouldContinueFalling);
                            if (mayReplace && wouldSurvive) {
                                if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level().getFluidState(pos).is(Fluids.WATER)) {
                                    this.blockState = this.blockState.setValue(BlockStateProperties.WATERLOGGED, true);
                                }

                                if (this.level().setBlock(pos, this.blockState, 3)) {
                                    serverLevel.getChunkSource()
                                            .chunkMap
                                            .sendToTrackingPlayers(this, new ClientboundBlockUpdatePacket(pos, this.level().getBlockState(pos)));
                                    this.discard();

                                    if (this.blockData != null && this.blockState.hasBlockEntity()) {
                                        BlockEntity blockEntity = this.level().getBlockEntity(pos);
                                        if (blockEntity != null) {
                                            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(
                                                    blockEntity.problemPath(), LOGGER
                                            )) {
                                                RegistryAccess registryAccess = this.level().registryAccess();
                                                TagValueOutput output = TagValueOutput.createWithContext(reporter, registryAccess);
                                                blockEntity.saveWithoutMetadata(output);
                                                CompoundTag merged = output.buildResult();
                                                this.blockData.forEach((name, tag) -> merged.put(name, tag.copy()));
                                                blockEntity.loadWithComponents(TagValueInput.create(reporter, registryAccess, merged));
                                            } catch (Exception var19) {
                                                LOGGER.error("Failed to load block entity from hoked block", var19);
                                            }

                                            blockEntity.setChanged();
                                        }
                                    }
                                } else if (this.dropItem && serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
                                    this.discard();
                                    this.spawnAtLocation(serverLevel, block);
                                }
                            } else {
                                this.discard();
                                if (this.dropItem && serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
                                    this.spawnAtLocation(serverLevel, block);
                                }
                            }
                        } else {
                            this.discard();
                        }
                    }
                }
            }
            this.causeMovingDamage();
            this.setDeltaMovement(this.getDeltaMovement().scale(0.91f));
        }
    }


    public void causeMovingDamage() {
        if(this.level() instanceof ServerLevel level) {
            double speedSqr = this.getDeltaMovement().lengthSqr();
            if (speedSqr >= 0.04) {
                Predicate<Entity> entitySelector = EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE);
                DamageSource actualDamageSource = this.blockState.getBlock() instanceof Fallable fallable
                        ? fallable.getFallDamageSource(this)
                        : this.damageSources().fallingBlock(this);
                float damage = (float) Math.min(10, (Math.sqrt(speedSqr) - 0.2) * 5);
                level.getEntities(this, this.getBoundingBox(), entitySelector).forEach(entity -> entity.hurtServer(level, actualDamageSource, damage));
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.store("BlockState", BlockState.CODEC, this.blockState);
        output.putInt("Time", this.time);
        output.putBoolean("DropItem", this.dropItem);
        output.putBoolean("HurtEntities", this.hurtEntities);
        output.putFloat("FallHurtAmount", this.fallDamagePerDistance);
        output.putInt("FallHurtMax", this.fallDamageMax);
        if (this.blockData != null) {
            output.store("TileEntityData", CompoundTag.CODEC, this.blockData);
        }

        output.putBoolean("CancelDrop", this.cancelDrop);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.blockState = input.read("BlockState", BlockState.CODEC).orElse(DEFAULT_BLOCK_STATE);
        this.time = input.getIntOr("Time", 0);
        boolean defaultHurtEntities = this.blockState.is(BlockTags.ANVIL);
        this.hurtEntities = input.getBooleanOr("HurtEntities", defaultHurtEntities);
        this.fallDamagePerDistance = input.getFloatOr("FallHurtAmount", 0.0F);
        this.fallDamageMax = input.getIntOr("FallHurtMax", 40);
        this.dropItem = input.getBooleanOr("DropItem", true);
        this.blockData = input.read("TileEntityData", CompoundTag.CODEC).orElse(null);
        this.cancelDrop = input.getBooleanOr("CancelDrop", false);
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public void fillCrashReportCategory(@NotNull CrashReportCategory category) {
        super.fillCrashReportCategory(category);
        category.setDetail("Immitating BlockState", this.blockState.toString());
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Override
    public @NotNull Packet<@NotNull ClientGamePacketListener> getAddEntityPacket(@NotNull ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity, Block.getId(this.getBlockState()));
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.blockState = Block.stateById(packet.getData());
        this.blocksBuilding = true;
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        this.setPos(x, y, z);
        this.setStartPos(this.blockPosition());
    }

    @Override
    public @Nullable Entity teleport(TeleportTransition transition) {
        ResourceKey<@NotNull Level> newDimension = transition.newLevel().dimension();
        ResourceKey<@NotNull Level> oldDimension = this.level().dimension();
        boolean fromOrToEnd = (oldDimension == Level.END || newDimension == Level.END) && oldDimension != newDimension;
        Entity newEntity = super.teleport(transition);
        this.forceTickAfterTeleportToDuplicate = newEntity != null && fromOrToEnd;
        return newEntity;
    }
}
