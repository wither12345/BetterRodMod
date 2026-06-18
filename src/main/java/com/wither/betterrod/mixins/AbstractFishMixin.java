package com.wither.betterrod.mixins;

import com.wither.betterrod.entity.AbstractFishInterface;
import com.wither.betterrod.entity.FishBeAttractedGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFish.class)
public abstract class AbstractFishMixin extends WaterAnimal implements AbstractFishInterface {
    @Unique @Nullable private Entity better_rod$attractor;

    protected AbstractFishMixin(EntityType<? extends @NotNull WaterAnimal> type, Level level) {
        super(type, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    protected void registerGoals(CallbackInfo info) {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FishBeAttractedGoal(this, this));
    }

    @Override
    public void better_rod$setAttracted(Entity attractor) {
        this.better_rod$attractor = attractor;
    }

    @Override
    public Entity better_rod$getAttracted() {
        return better_rod$attractor;
    }
}
