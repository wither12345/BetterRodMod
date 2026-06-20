package com.wither.betterrod.item;

import com.wither.betterrod.entity.NetherBrickHookEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;

public class NetherBrickHookItem extends HookItem{
    public NetherBrickHookItem(Properties properties) {
        super(properties);
    }

    @Override
    public FishingHook getHook(Player player, Level level, int luck, int lureSpeed) {
        return new NetherBrickHookEntity(player, level, luck, lureSpeed);
    }
}
