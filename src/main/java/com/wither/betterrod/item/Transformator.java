package com.wither.betterrod.item;

import com.wither.betterrod.item.components.FishingEquipmentSlot;

public class Transformator extends RodEquipmentItem{
    public Transformator(Properties properties) {
        super(properties);
    }

    @Override
    public FishingEquipmentSlot getSlot() {
        return FishingEquipmentSlot.ACCESSORY;
    }
}
