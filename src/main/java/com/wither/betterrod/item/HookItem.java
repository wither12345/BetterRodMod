package com.wither.betterrod.item;

import com.wither.betterrod.item.components.FishingRodComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;

public abstract class HookItem extends RodEquipmentItem{
    public HookItem(Properties properties) {super(properties);}

    @Override
    public FishingRodComponents.FishingEquipmentSlot getSlot() {
        return FishingRodComponents.FishingEquipmentSlot.HOOK;
    }

    public FishingHook getHook(Player player, Level level, int luck, int lureSpeed){
        return new FishingHook(player, level, luck, lureSpeed);
    }
}
