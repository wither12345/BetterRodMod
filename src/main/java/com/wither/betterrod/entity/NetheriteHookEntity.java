package com.wither.betterrod.entity;

import com.wither.betterrod.Config;
import com.wither.betterrod.init.EntityRegister;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NetheriteHookEntity extends FishingHook {

    public static final ResourceKey<@NotNull LootTable> NETHER_LAVA_FISH = ResourceKey.create(Registries.LOOT_TABLE, Identifier.parse("better_rod:gameplay/fishing/nether/lava_fishing"));
    public static final ResourceKey<@NotNull LootTable> DEFAULT_LAVA_FISH = ResourceKey.create(Registries.LOOT_TABLE, Identifier.parse("better_rod:gameplay/fishing/default/lava_fishing"));
    private int outOfLavaTime;
    public NetheriteHookEntity(Player player, Level level, int luck, int lureSpeed) {
        super(player, level, luck, lureSpeed);
    }

    public NetheriteHookEntity(EntityType<? extends @NotNull NetheriteHookEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if(!Config.LAVA_FISHING.get()) return;
        float liquidHeight = 0.0F;
        BlockPos blockPos = this.blockPosition();
        FluidState fluidState = this.level().getFluidState(blockPos);
        if (fluidState.is(FluidTags.LAVA)) {
            liquidHeight = fluidState.getHeight(this.level(), blockPos);
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.08, 0.0));
        }

        boolean isInLava = liquidHeight > 0.0F;
        if (this.currentState == FishingHook.FishHookState.FLYING) {
            if (this.getHookedIn() != null) {
                this.setDeltaMovement(Vec3.ZERO);
                this.currentState = FishingHook.FishHookState.HOOKED_IN_ENTITY;
                return;
            }

            if (isInLava) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.3, 0.2, 0.3));
                this.currentState = FishingHook.FishHookState.BOBBING;
                return;
            }
        }
        if(this.currentState == FishHookState.BOBBING){
            Vec3 movement = this.getDeltaMovement();
            double force = this.getY() + movement.y - blockPos.getY() - liquidHeight;
            if (Math.abs(force) < 0.01) {
                force += Math.signum(force) * 0.1;
            }

            this.setDeltaMovement(movement.x * 0.9, movement.y - force * this.random.nextFloat() * 0.2, movement.z * 0.9);

            if (isInLava) {
                this.outOfLavaTime = Math.max(0, this.outOfLavaTime - 1);

                if (this.biting) {
                    this.setDeltaMovement(
                            this.getDeltaMovement().add(0.0, -0.05 * this.syncronizedRandom.nextFloat() * this.syncronizedRandom.nextFloat(), 0.0)
                    );
                }

                if (!this.level().isClientSide()) {
                    this.catchingLavaFish(blockPos);
                }


            } else {
                this.outOfLavaTime = Math.min(10, this.outOfLavaTime + 1);
            }
        }
    }

    @Override
    public int retrieve(@NotNull ItemStack rod){
        Player owner = this.getPlayerOwner();
        BlockPos blockPos = this.blockPosition();
        FluidState fluidState = this.level().getFluidState(blockPos);
        if (Config.LAVA_FISHING.get() && !this.level().isClientSide() && owner != null && !this.shouldStopFishing(owner) && fluidState.is(FluidTags.LAVA)) {
            int dmg = 0 ;
            if (this.nibble > 0) {
                if(this.level().dimension() == Level.NETHER) {
                    if(this.random.nextFloat() < 0.25F){
                        MagmaCube cube = new MagmaCube(EntityType.MAGMA_CUBE, this.level());
                        cube.setPos(this.getX(), this.getY(), this.getZ());
                        cube.setSize(5, true);
                        this.level().addFreshEntity(cube);
                        this.pullEntity(cube);
                        dmg = 3;
                    }
                    else
                        dmg = dropItem(rod, NETHER_LAVA_FISH);
                }
                else
                    dmg = dropItem(rod, DEFAULT_LAVA_FISH);
                /*
                LootParams params = new LootParams.Builder((ServerLevel)this.level())
                        .withParameter(LootContextParams.ORIGIN, this.position())
                        .withParameter(LootContextParams.TOOL, rod)
                        .withParameter(LootContextParams.THIS_ENTITY, this)
                        .withParameter(LootContextParams.ATTACKING_ENTITY, this.getOwner())
                        .withLuck(this.luck + owner.getLuck())
                        .create(LootContextParamSets.FISHING);
                LootTable lootTable = this.level().getServer().reloadableRegistries().getLootTable(NETHER_LAVA_FISH);
                List<ItemStack> items = lootTable.getRandomItems(params);
                event = new ItemFishedEvent(items, this.onGround() ? 2 : 1, this);
                NeoForge.EVENT_BUS.post(event);
                if (event.isCanceled()) {
                    this.discard();
                    return event.getRodDamage();
                }
                CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)owner, rod, this, items);

                for (ItemStack itemStack : items) {
                    ItemEntity entity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemStack);
                    double xa = owner.getX() - this.getX();
                    double ya = owner.getY() - this.getY();
                    double za = owner.getZ() - this.getZ();
                    if(entity instanceof ItemEntityInterface itemEntityInterface)
                        itemEntityInterface.better_rod$setFromLava(true);
                    entity.setDeltaMovement(xa * 0.1, ya * 0.1 + Math.sqrt(Math.sqrt(xa * xa + ya * ya + za * za)) * 0.08, za * 0.1);
                    this.level().addFreshEntity(entity);
                    owner.level()
                            .addFreshEntity(new ExperienceOrb(owner.level(), owner.getX(), owner.getY() + 0.5, owner.getZ() + 0.5, this.random.nextInt(6) + 1));
                    if (itemStack.is(ItemTags.FISHES)) {
                        owner.awardStat(Stats.FISH_CAUGHT, 1);
                    }
                }

                 */

            }
            this.discard();
            return dmg;
        }
        return super.retrieve(rod);
    }

    private int dropItem(@NotNull ItemStack rod, ResourceKey<@NotNull LootTable> loot){
        ItemFishedEvent event;
        Player owner = this.getPlayerOwner();
        LootParams params = new LootParams.Builder((ServerLevel)this.level())
                .withParameter(LootContextParams.ORIGIN, this.position())
                .withParameter(LootContextParams.TOOL, rod)
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .withParameter(LootContextParams.ATTACKING_ENTITY, this.getOwner())
                .withLuck(this.luck + owner.getLuck())
                .create(LootContextParamSets.FISHING);
        LootTable lootTable = this.level().getServer().reloadableRegistries().getLootTable(loot);
        List<ItemStack> items = lootTable.getRandomItems(params);
        event = new ItemFishedEvent(items, 2, this);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            this.discard();
            return event.getRodDamage();
        }
        CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)owner, rod, this, items);

        for (ItemStack itemStack : items) {
            ItemEntity entity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemStack);
            double xa = owner.getX() - this.getX();
            double ya = owner.getY() - this.getY();
            double za = owner.getZ() - this.getZ();
            if(entity instanceof ItemEntityInterface itemEntityInterface) {
                itemEntityInterface.better_rod$setFromLava(true);
            }
            entity.setDeltaMovement(xa * 0.1, ya * 0.1 + Math.sqrt(Math.sqrt(xa * xa + ya * ya + za * za)) * 0.08, za * 0.1);
            this.level().addFreshEntity(entity);
            owner.level()
                    .addFreshEntity(new ExperienceOrb(owner.level(), owner.getX(), owner.getY() + 0.5, owner.getZ() + 0.5, this.random.nextInt(6) + 1));
            if (itemStack.is(ItemTags.FISHES)) {
                owner.awardStat(Stats.FISH_CAUGHT, 1);
            }
        }
        return event.getRodDamage();

    }

    @Override
    public @NotNull EntityType<?> getType() {
        return EntityRegister.NETHERITE_HOOK.get();
    }

    private void catchingLavaFish(BlockPos blockPos) {
        ServerLevel serverLevel = (ServerLevel)this.level();
        int fishingSpeed = 1;
        BlockPos above = blockPos.above();
        if (this.random.nextFloat() < 0.25F && this.level().isRainingAt(above)) {
            fishingSpeed++;
        }

        if (this.random.nextFloat() < 0.5F && !this.level().canSeeSky(above)) {
            fishingSpeed--;
        }

        if (this.nibble > 0) {
            this.nibble--;
            if (this.nibble <= 0) {
                this.timeUntilLured = 0;
                this.timeUntilHooked = 0;
                this.getEntityData().set(DATA_BITING, false);
            }
        } else if (this.timeUntilHooked > 0) {
            this.timeUntilHooked -= fishingSpeed;
            if (this.timeUntilHooked > 0) {
                this.fishAngle = this.fishAngle + (float)this.random.triangle(0.0, 9.188);
                float angle = this.fishAngle * (float) (Math.PI / 180.0);
                float angleSin = Mth.sin(angle);
                float angleCos = Mth.cos(angle);
                double fishX = this.getX() + angleSin * this.timeUntilHooked * 0.1F;
                double fishY = Mth.floor(this.getY()) + 1.0F;
                double fishZ = this.getZ() + angleCos * this.timeUntilHooked * 0.1F;
                BlockState splashBlockState = serverLevel.getBlockState(BlockPos.containing(fishX, fishY - 1.0, fishZ));
                if (splashBlockState.is(Blocks.LAVA)) {
                    if (this.random.nextFloat() < 0.15F) {
                        serverLevel.sendParticles(ParticleTypes.LAVA, fishX, fishY - 0.1F, fishZ, 1, angleSin, 0.1, angleCos, 0.0);
                    }

                    float particleXMovement = angleSin * 0.04F;
                    float particleZMovement = angleCos * 0.04F;
                    serverLevel.sendParticles(ParticleTypes.LAVA, fishX, fishY, fishZ, 0, particleZMovement, 0.01, -particleXMovement, 1.0);
                    serverLevel.sendParticles(ParticleTypes.LAVA, fishX, fishY, fishZ, 0, -particleZMovement, 0.01, particleXMovement, 1.0);
                }
            } else {
                this.playSound(SoundEvents.LAVA_EXTINGUISH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                double y = this.getY() + 0.5;
                serverLevel.sendParticles(
                        ParticleTypes.LAVA, this.getX(), y, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), this.getBbWidth(), 0.0, this.getBbWidth(), 0.2F
                );
                serverLevel.sendParticles(
                        ParticleTypes.LAVA,
                        this.getX(),
                        y,
                        this.getZ(),
                        (int)(1.0F + this.getBbWidth() * 20.0F),
                        this.getBbWidth(),
                        0.0,
                        this.getBbWidth(),
                        0.2F
                );
                this.nibble = Mth.nextInt(this.random, 20, 40);
                this.getEntityData().set(DATA_BITING, true);
            }
        } else if (this.timeUntilLured > 0) {
            this.timeUntilLured -= fishingSpeed;
            float teaseChance = 0.15F;
            if (this.timeUntilLured < 20) {
                teaseChance += (20 - this.timeUntilLured) * 0.05F;
            } else if (this.timeUntilLured < 40) {
                teaseChance += (40 - this.timeUntilLured) * 0.02F;
            } else if (this.timeUntilLured < 60) {
                teaseChance += (60 - this.timeUntilLured) * 0.01F;
            }

            if (this.random.nextFloat() < teaseChance) {
                float angle = Mth.nextFloat(this.random, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
                float dist = Mth.nextFloat(this.random, 25.0F, 60.0F);
                double fishX = this.getX() + Mth.sin(angle) * dist * 0.1;
                double fishY = Mth.floor(this.getY()) + 1.0F;
                double fishZ = this.getZ() + Mth.cos(angle) * dist * 0.1;
                BlockState splashBlockState = serverLevel.getBlockState(BlockPos.containing(fishX, fishY - 1.0, fishZ));
                if (splashBlockState.is(Blocks.LAVA)) {
                    serverLevel.sendParticles(ParticleTypes.LAVA, fishX, fishY, fishZ, 2 + this.random.nextInt(2), 0.1F, 0.0, 0.1F, 0.0);
                }
            }

            if (this.timeUntilLured <= 0) {
                this.fishAngle = Mth.nextFloat(this.random, 0.0F, 360.0F);
                this.timeUntilHooked = Mth.nextInt(this.random, 20, 80);
            }
        } else {
            this.timeUntilLured = Mth.nextInt(this.random, 100, 600);
            this.timeUntilLured = this.timeUntilLured - this.lureSpeed;
        }
    }
}
