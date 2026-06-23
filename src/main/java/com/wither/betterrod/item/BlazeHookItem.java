package com.wither.betterrod.item;

import com.wither.betterrod.entity.BlazeHookEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class BlazeHookItem extends HookItem{
    public BlazeHookItem(Properties properties) {super(properties);}

    @Override
    public FishingHook getHook(Player player, Level level, int luck, int lureSpeed) {return new BlazeHookEntity(player, level, luck, lureSpeed);}

    @Override
    public void onPull(Projectile hook, Entity hookedIn) {
        hookedIn.setRemainingFireTicks(100);
    }
}
