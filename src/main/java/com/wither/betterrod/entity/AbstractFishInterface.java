package com.wither.betterrod.entity;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface AbstractFishInterface {
    void better_rod$setAttracted(@Nullable Entity attractor);
    Entity better_rod$getAttracted();
}
