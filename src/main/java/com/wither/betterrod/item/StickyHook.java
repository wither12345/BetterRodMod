package com.wither.betterrod.item;

import com.wither.betterrod.entity.StickyHookEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;

public class StickyHook extends HookItem{
    public StickyHook(Properties properties) {
        super(properties);
    }

    @Override
    public FishingHook getHook(Player player, Level level, int luck, int lureSpeed) {
        return new StickyHookEntity(player,level,luck,lureSpeed);
    }
}
