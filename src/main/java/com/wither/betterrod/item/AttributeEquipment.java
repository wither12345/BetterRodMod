package com.wither.betterrod.item;

import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public interface AttributeEquipment {
    void modifyAttribute(ItemAttributeModifierEvent event);
}
