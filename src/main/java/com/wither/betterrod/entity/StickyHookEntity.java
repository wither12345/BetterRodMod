package com.wither.betterrod.entity;

import com.wither.betterrod.init.EntityRegister;
import com.wither.betterrod.item.HookInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class StickyHookEntity extends FishingHook {
    public StickyHookEntity(Player player, Level level, int luck, int lureSpeed) {
        super(player, level, luck, lureSpeed);
    }

    public StickyHookEntity(EntityType<? extends @NotNull StickyHookEntity> type, Level level) {
        super(type, level);
    }

    public void onHookedBlock(BlockPos pos) {
        BlockState state = this.level().getBlockState(pos);
        if(!state.requiresCorrectToolForDrops() && state.getDestroySpeed(level(), pos) > 0) {
            HookedBlockEntity entity = HookedBlockEntity.beHooked(this.level(), pos, state, this);
            if (this instanceof HookInterface hookInterface)
                hookInterface.better_rod$forceSetHooked(entity);
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        this.setDeltaMovement(Vec3.ZERO);
        if(this.getHookedIn() == null && !this.level().isClientSide() && checkCanHook(hitResult)) {
            this.onHookedBlock(hitResult.getBlockPos());
        }

    }

    private boolean checkCanHook(BlockHitResult hitResult){
        if(this.getOwner() instanceof Player player ) {
            GameType mode = player.gameMode();
            if(mode != null)
                return !mode.isBlockPlacingRestricted();
        }
        return false;
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return EntityRegister.STICKY_HOOK.get();
    }
}
