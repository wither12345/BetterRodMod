package com.wither.betterrod.item;

import com.wither.betterrod.item.components.FishingRodComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public interface HookInterface {
    void better_rod$setEquipmentItem(FishingRodComponents.FishingEquipmentSlot slot, ItemStack item);
    void better_rod$swing();
    void better_rod$forceSetHooked(Entity entity);
    void better_rod$setTimeUntilLured(int time);
    int better_rod$getTimeUntilLured();
    void better_rod$setTimeUntilHooked(int time);
    int better_rod$getTimeUntilHooked();
}
