package com.wither.betterrod.item;

public class Transformator extends RodEquipmentItem{
    public Transformator(Properties properties) {
        super(properties);
    }

    @Override
    public FishingEquipmentSlot getSlot() {
        return FishingEquipmentSlot.ACCESSORY;
    }
}
