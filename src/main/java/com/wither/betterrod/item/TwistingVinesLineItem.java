package com.wither.betterrod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TwistingVinesLineItem extends LineItem{
    public TwistingVinesLineItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onTick(Projectile hook, @Nullable Entity hookedIn) {
        if(hook.level().getGameTime() % 20 == 0 && hook.getOwner() != null) {
            Vec3 vec0 = hook.position();
            Vec3 vec1 = hook.getOwner().position().add(0,1.5,0);
            for(Entity entity: getEntities(hook, hookedIn, vec0, vec1)){
                if (entity instanceof LivingEntity living) {
                    AABB entityBox = entity.getBoundingBox();
                    if (lineIntersectsAABB(vec0, vec1, entityBox)) {
                        living.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 1));
                    }
                }
            }
        }
    }
}
