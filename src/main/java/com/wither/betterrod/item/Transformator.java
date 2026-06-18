package com.wither.betterrod.item;

import com.wither.betterrod.item.components.FishingRodComponents;

public class Transformator extends RodEquipmentItem{
    public Transformator(Properties properties) {
        super(properties);
    }

    @Override
    public FishingRodComponents.FishingEquipmentSlot getSlot() {
        return FishingRodComponents.FishingEquipmentSlot.ACCESSORY;
    }
}
