package com.wither.betterrod.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class RodEquipmentItem extends Item {
    public RodEquipmentItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public abstract FishingEquipmentSlot getSlot();

    public void modifyHook(FishingHook hook, ItemStack itemStack) {
        if(hook instanceof HookInterface hookInterface)
            hookInterface.better_rod$setEquipmentItem(this.getSlot(), itemStack);
    }

    public void onPull(Projectile hook, Entity hookedIn){}

    public void onTick(Projectile hook, @Nullable Entity hookedIn){}
}
