package com.wither.betterrod.item;

import com.wither.betterrod.entity.NetheriteHookEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;

public class NetheriteSpikedHookItem extends SpikedHookItem{
    public NetheriteSpikedHookItem(Properties properties, float damage) {
        super(properties, damage);
    }

    @Override
    public FishingHook getHook(Player player, Level level, int luck, int lureSpeed) {
        return new NetheriteHookEntity(player, level, luck, lureSpeed);
    }
}
