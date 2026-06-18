package com.wither.betterrod.item;

import com.wither.betterrod.BetterRodMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.NotNull;

public class SpikedHookItem extends HookItem {
    public static final ResourceKey<@NotNull DamageType> TATER_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(BetterRodMod.MODID, "hook"));

    private final float damage ;
    public SpikedHookItem(Properties properties, float damage) {
        super(properties);
        this.damage = damage;
    }

    @Override
    public void onPull(Projectile hook, Entity hookedIn) {
        damage(hook, hookedIn, damage);
    }

    @Override
    public void onTick(Projectile hook, Entity hookedIn) {
        if(hookedIn != null)
            BetterRodMod.LOGGER.info(String.valueOf(hookedIn.getDeltaMovement().lengthSqr()));
        if(hookedIn != null && hookedIn.getDeltaMovement().lengthSqr() > 2 && hook instanceof HookInterface hookInterface){
            damage(hook, hookedIn, damage * 0.4f);
            hookedIn.setDeltaMovement(hookedIn.getDeltaMovement().multiply(0.2,0.2,0.2));
            hookInterface.better_rod$forceSetHooked(null);
        }

    }

    private static void damage(Projectile hook, Entity entity, float dmg){
        if(entity.level() instanceof ServerLevel serverLevel) {
            if (hook.getOwner() instanceof Player player)
                entity.hurtServer(serverLevel, hook.getOwner().damageSources().source(TATER_DAMAGE, player), dmg);
            else if(hook.getOwner() instanceof LivingEntity living)
                entity.hurtServer(serverLevel, hook.getOwner().damageSources().source(TATER_DAMAGE, living), dmg);
        }
    }
}
