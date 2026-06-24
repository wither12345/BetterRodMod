package com.wither.betterrod.entity;

import com.wither.betterrod.item.HookInterface;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;

import java.util.EnumSet;

public class FishBeAttractedGoal extends Goal {
    AbstractFishInterface abstractFishInterface;
    WaterAnimal animal;
    Entity wantedEntity ;
    boolean beHooked ;
    int eating_cooldown;

    public FishBeAttractedGoal(AbstractFishInterface abstractFishInterface, WaterAnimal animal){
        this.abstractFishInterface = abstractFishInterface;
        this.animal = animal;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    private boolean wantedEntityAvailable(){
        if(wantedEntity instanceof FishingHook hook)
            return (hook.getHookedIn() == null || hook.getHookedIn() == animal) && hook.isAlive();
        return wantedEntity != null && wantedEntity.isAlive();
    }

    private void targetToAttractedPlace(float dy){
        this.animal.getNavigation().moveTo(this.wantedEntity.getX(), this.wantedEntity.getY() - dy, this.wantedEntity.getZ(), 1);
    }

    @Override
    public boolean canUse() {
        beHooked = false;
        wantedEntity = abstractFishInterface.better_rod$getAttracted();
        return wantedEntityAvailable();
    }

    @Override
    public boolean canContinueToUse() {
        return wantedEntityAvailable();
    }

    @Override
    public void start() {
        targetToAttractedPlace(wantedEntity instanceof FishingHook ? 1 : 0);
    }

    @Override
    public void stop() {
        this.wantedEntity = null;
    }

    @Override
    public void tick() {
        super.tick();
        if(wantedEntity instanceof FishingHook hook) {
            if (beHooked) {
                animal.setPos(this.wantedEntity.position().subtract(0, 0.5, 0).subtract(this.animal.getLookAngle().multiply(1, 0, 1).normalize().multiply(0.2, 0, 0.2)));
            }
            else if (this.animal.getNavigation().isDone()) {
                if (this.animal.distanceTo(wantedEntity) < 2 && hook.getHookedIn() == null && wantedEntity instanceof HookInterface hookInterface) {
                    beHooked = true;
                    hookInterface.better_rod$forceSetHooked(animal);
                    this.animal.getNavigation().stop();
                } else
                    targetToAttractedPlace(1);
            }
        }
        else if(wantedEntity instanceof ItemEntity itemEntity){
            if(eating_cooldown > 0)
                eating_cooldown --;
            else if (this.animal.getNavigation().isDone()) {
                if (this.animal.distanceTo(wantedEntity) < 2) {
                    eating_cooldown = 200;
                    this.animal.playSound(SoundEvents.GENERIC_EAT.value(), 1.0F, 1.0F);
                    itemEntity.getItem().shrink(1);
                    this.animal.getNavigation().stop();
                } else
                    targetToAttractedPlace(0);
            }
        }
    }
}
