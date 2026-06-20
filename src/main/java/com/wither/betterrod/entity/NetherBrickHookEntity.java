package com.wither.betterrod.entity;

import com.wither.betterrod.init.EntityRegister;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class NetherBrickHookEntity extends FireproofHookEntity {
    public NetherBrickHookEntity(Player player, Level level, int luck, int lureSpeed) {
        super(player, level, luck, lureSpeed);
    }

    public NetherBrickHookEntity(EntityType<? extends @NotNull NetherBrickHookEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected Entity catchingNetherEntity() {
        Entity entity ;
        if(this.random.nextFloat() < 0.2F){
            MagmaCube cube = new MagmaCube(EntityType.MAGMA_CUBE, this.level());
            cube.setSize(5, true);
            entity = cube;
        }
        else if(this.random.nextFloat() < 0.5F){
            entity = new Blaze(EntityType.BLAZE, this.level());
        }
        else {
            entity = new WitherSkeleton(EntityType.WITHER_SKELETON, this.level());
        }
        return entity;
    }

    @Override
    protected float entityChance() {
        return 0.75f;
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return EntityRegister.NETHER_BRICK_HOOK.get();
    }
}
