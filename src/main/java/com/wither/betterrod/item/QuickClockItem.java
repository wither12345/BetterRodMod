package com.wither.betterrod.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.Nullable;

public class QuickClockItem extends AccessoryItem {
    public QuickClockItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onTick(Projectile hook, @Nullable Entity hookedIn) {
        if(hook instanceof HookInterface fishingHook){
            if(fishingHook.better_rod$getTimeUntilHooked() > 0)
                fishingHook.better_rod$setTimeUntilHooked(Math.max(fishingHook.better_rod$getTimeUntilHooked() - 2, 1));
            else if(fishingHook.better_rod$getTimeUntilLured() > 0)
                fishingHook.better_rod$setTimeUntilLured(Math.max(fishingHook.better_rod$getTimeUntilLured() - 1, 1));
        }
    }
}
