package com.wither.betterrod.item;

import com.wither.betterrod.init.ItemComponentsRegister;
import com.wither.betterrod.item.components.FishingRodComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber
public class AddTooltip {
    @SubscribeEvent
    public static void modifyTooltip(ItemTooltipEvent event){
        ItemStack itemStack = event.getItemStack();
        FishingRodComponents components = itemStack.get(ItemComponentsRegister.FISHING_ROD);
        if(components != null){
            components.modifyTooltip(event.getToolTip());
        }
    }
}
