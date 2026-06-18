package com.wither.betterrod.mixins;

import com.wither.betterrod.init.ItemComponentsRegister;
import com.wither.betterrod.item.HookInterface;
import com.wither.betterrod.item.RodEquipmentItem;
import com.wither.betterrod.item.components.BaitComponent;
import com.wither.betterrod.item.components.FishingRodComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin extends Projectile implements HookInterface {
    @Unique private ItemStack better_rod$LineItem = ItemStack.EMPTY;
    @Unique private ItemStack better_rod$HookItem = ItemStack.EMPTY;
    @Unique private ItemStack better_rod$AccessoryItem = ItemStack.EMPTY;
    @Unique private ItemStack better_rod$baitItem = ItemStack.EMPTY ;
    @Unique private int better_rod$baitTick = 0 ;
    @Shadow @Nullable private Entity hookedIn;
    @Final @Shadow private static EntityDataAccessor<@NotNull Integer> DATA_HOOKED_ENTITY;
    @Mutable @Final @Shadow protected final int lureSpeed;
    @Shadow protected int timeUntilLured;
    @Shadow protected int timeUntilHooked;
    @Shadow public FishingHook.FishHookState currentState = FishingHook.FishHookState.FLYING;

    protected FishingHookMixin(EntityType<? extends @NotNull Projectile> type, Level level) {
        super(type, level);
        lureSpeed = 0 ;
    }


    @Inject(method = "setOwner", at = @At("TAIL"))
    private void setOwner(@Nullable Entity owner, CallbackInfo info){
        if(owner instanceof LivingEntity entity) {
            ItemStack offHand = entity.getItemInHand(InteractionHand.OFF_HAND);
            BaitComponent component = offHand.get(ItemComponentsRegister.BAIT);
            if (!offHand.isEmpty() && component != null) {
                this.better_rod$baitItem = offHand.split(1);
                better_rod$baitTick = (int)(component.min_tick() * ((500 - lureSpeed) / 500f));
            }
        }
    }

    @Inject(method = "pullEntity", at = @At("TAIL"))
    private void pullEntity(Entity entity, CallbackInfo info){
        if(entity.isInWater())
            entity.push(new Vec3(0,0.5,0));
        if(better_rod$AccessoryItem.getItem() instanceof RodEquipmentItem item)
            item.onPull(this, entity);
        if(better_rod$HookItem.getItem() instanceof RodEquipmentItem item)
            item.onPull(this, entity);
        if(better_rod$LineItem.getItem() instanceof RodEquipmentItem item)
            item.onPull(this, entity);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo info){
        if(better_rod$AccessoryItem.getItem() instanceof RodEquipmentItem item)
            item.onTick(this, hookedIn);
        if(better_rod$LineItem.getItem() instanceof RodEquipmentItem item)
            item.onTick(this, hookedIn);
        if(better_rod$HookItem.getItem() instanceof RodEquipmentItem item)
            item.onTick(this, hookedIn);
        if(better_rod$baitTick > 0)
            better_rod$baitTick -- ;
        else if(!better_rod$baitItem.isEmpty() && !this.level().isClientSide() && this.isInWater())
            BaitComponent.attracting(this, better_rod$baitItem);
        if(this.currentState == FishingHook.FishHookState.HOOKED_IN_ENTITY && this.hookedIn == null)
            this.currentState = FishingHook.FishHookState.FLYING;
    }


    @Inject(method = "setHookedEntity", at = @At("TAIL"))
    private void setHookedEntity(@Nullable Entity hookedIn, CallbackInfo info){
        PotionContents contents = better_rod$HookItem.get(DataComponents.POTION_CONTENTS);
        if(contents != null && hookedIn instanceof LivingEntity living)
            contents.applyToLivingEntity(living, better_rod$HookItem.getOrDefault(DataComponents.POTION_DURATION_SCALE, 1.0F));
    }


    @Inject(method = "catchingFish", at = @At("HEAD"), cancellable = true)
    private void catchingFish(BlockPos blockPos, CallbackInfo info) {
        if(this.better_rod$baitItem != ItemStack.EMPTY)
            info.cancel();

    }

    @Override
    public void better_rod$setEquipmentItem(FishingRodComponents.FishingEquipmentSlot slot, ItemStack item) {
        switch (slot){
            case LINE -> this.better_rod$LineItem = item;
            case HOOK -> this.better_rod$HookItem = item;
            case ACCESSORY -> this.better_rod$AccessoryItem = item;
        }
    }

    @Override
    public void better_rod$swing() {
        if(hookedIn != null && this.getOwner() != null){
            Vec3 d = this.getOwner().position().subtract(hookedIn.position()).normalize();
            hookedIn.push(this.getOwner().getLookAngle().add(d));
        }
    }

    @Override
    public void better_rod$forceSetHooked(Entity entity) {
        this.hookedIn = entity;
        this.getEntityData().set(DATA_HOOKED_ENTITY, hookedIn == null ? 0 : hookedIn.getId() + 1);
    }

    @Override
    public int better_rod$getTimeUntilLured() {return timeUntilLured;}
    @Override
    public void better_rod$setTimeUntilLured(int time) {this.timeUntilLured = time;}
    @Override
    public int better_rod$getTimeUntilHooked() {return this.timeUntilHooked;}
    @Override
    public void better_rod$setTimeUntilHooked(int time) {this.timeUntilHooked = time;}
}
