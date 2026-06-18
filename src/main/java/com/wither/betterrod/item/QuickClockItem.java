package com.wither.betterrod.item;

import com.wither.betterrod.item.components.FishingRodComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.Nullable;

public class QuickClockItem extends RodEquipmentItem {
    public QuickClockItem(Properties properties) {
        super(properties);
    }

    @Override
    public FishingRodComponents.FishingEquipmentSlot getSlot() {
        return FishingRodComponents.FishingEquipmentSlot.ACCESSORY;
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
