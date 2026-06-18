package com.wither.betterrod.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class WindHookItem extends HookItem{
    public WindHookItem(Properties properties) {super(properties);}

    @Override
    public void onPull(Projectile hook, Entity hookedIn) {
        super.onPull(hook, hookedIn);

        Predicate<Entity> entitySelector = EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        Entity owner = hook.getOwner();
        if(owner == null)return;
        hook.level().getEntities(hookedIn, hookedIn.getBoundingBox().inflate(3), entitySelector).forEach(
                entity -> {
                    Vec3 delta = owner.position().subtract(entity.position()).scale(0.075);
                    entity.push(delta);
                }
        );
    }
}
