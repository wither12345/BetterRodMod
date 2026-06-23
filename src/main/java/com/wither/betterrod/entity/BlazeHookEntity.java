package com.wither.betterrod.entity;

import com.wither.betterrod.init.EntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class BlazeHookEntity extends FireproofHookEntity{
    public BlazeHookEntity(Player player, Level level, int luck, int lureSpeed) {super(player, level, luck, lureSpeed);}

    public BlazeHookEntity(EntityType<? extends @NotNull BlazeHookEntity> type, Level level) {super(type, level);}

    @Override
    protected float entityChance() {
        return 0.2f;
    }

    @Override
    protected Entity catchingNetherEntity() {
        return new Blaze(EntityType.BLAZE, this.level());
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return EntityRegister.BLAZE_HOOK.get();
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos center = this.getOnPos();
        BlockPos minPos = center.offset(-1,-1,-1);
        BlockPos maxPos = center.offset(1,1,1);
        for(BlockPos pos : BlockPos.betweenClosed(minPos,maxPos)){
            if(level().getBlockState(pos).is(Blocks.ICE)) {
                level().destroyBlock(pos, true, this.getOwner());
                level().setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
            }
        }
    }
}
