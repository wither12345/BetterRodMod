package com.wither.betterrod.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class ElasticLineItem extends RodEquipmentItem{
    public ElasticLineItem(Properties properties) {
        super(properties);
    }

    @Override
    public FishingEquipmentSlot getSlot() {
        return FishingEquipmentSlot.LINE;
    }

    @Override
    public void onTick(Projectile hook, Entity hookedIn) {
        Entity owner = hook.getOwner();
        if (owner != null) {
            if (hook.distanceToSqr(owner) > 25) {
                Vec3 delta = owner.position().subtract(hook.position()).scale(0.08);
                Objects.requireNonNullElse(hookedIn, hook).push(delta);
            }
        }
    }

    @Override
    public void modifyHook(FishingHook hook, ItemStack itemStack) {
        super.modifyHook(hook, itemStack);
        hook.setDeltaMovement(hook.getKnownMovement().scale(0.75));
    }
}
