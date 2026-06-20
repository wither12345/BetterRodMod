package com.wither.betterrod.entity;

import com.wither.betterrod.init.EntityRegister;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class NetheriteHookEntity extends FireproofHookEntity {
    public NetheriteHookEntity(Player player, Level level, int luck, int lureSpeed) {
        super(player, level, luck, lureSpeed);
    }

    public NetheriteHookEntity(EntityType<? extends @NotNull NetheriteHookEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return EntityRegister.NETHERITE_HOOK.get();
    }

    @Override
    protected Entity catchingNetherEntity() {
        MagmaCube cube = new MagmaCube(EntityType.MAGMA_CUBE, this.level());
        cube.setSize(4, true);
        return cube;
    }

    @Override
    protected float entityChance() {
        return 0.15f;
    }
}
