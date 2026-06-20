package com.wither.betterrod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EnderLineItem extends RodEquipmentItem{
    public EnderLineItem(Properties properties) {
        super(properties);
    }

    @Override
    public FishingEquipmentSlot getSlot() {
        return FishingEquipmentSlot.LINE;
    }

    @Override
    public void onPull(Projectile hook, Entity entity) {
        if(entity.level() instanceof ServerLevel && hook.getOwner() != null) {
            Vec3 delta = new Vec3(hook.getOwner().getX() - entity.getX(), hook.getOwner().getY() - entity.getY(), hook.getOwner().getZ() - entity.getZ()).scale(0.1);
            Vec3 vec = delta.multiply(5,0,5);
            teleport(entity,
                    entity.getX() + vec.x,
                    entity.getY() + vec.y,
                    entity.getZ() + vec.z
            );
            entity.setDeltaMovement(entity.getDeltaMovement().subtract(delta));
        }
    }

    private void teleport(Entity entity, double x, double y, double z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);

        while (pos.getY() < entity.level().getMaxY() && entity.level().getBlockState(pos).isSolidRender()) {
            pos.move(Direction.UP);
            y ++;
        }

        Vec3 oldPos = entity.position();
        entity.teleportTo(x, y, z);
        entity.level().gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of(entity));
        if (!entity.isSilent()) {
            entity.level().playSound(null, entity.xo, entity.yo, entity.zo, SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 1.0F, 1.0F);
            entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }
}
