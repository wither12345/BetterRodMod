package com.wither.betterrod.item;

import com.wither.betterrod.item.components.FishingEquipmentSlot;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public abstract class AccessoryItem extends RodEquipmentItem{
    public AccessoryItem(Properties properties) {super(properties);}

    @Override
    public FishingEquipmentSlot getSlot() {return FishingEquipmentSlot.ACCESSORY;}

    public void modifyLoot(ObjectArrayList<ItemStack> originalLoot, Projectile hook, ServerLevel level) {}
}
