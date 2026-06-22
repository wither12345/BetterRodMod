package com.wither.betterrod.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class LuckyCloverItem extends AccessoryItem implements AttributeEquipment{
    private static final Identifier attr = Identifier.parse("better_rod:lucky_clover");

    public LuckyCloverItem(Properties properties) {
        super(properties);
    }

    @Override
    public void modifyAttribute(ItemAttributeModifierEvent event) {
        event.addModifier(Attributes.LUCK, new AttributeModifier(attr, 5, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
    }
}
