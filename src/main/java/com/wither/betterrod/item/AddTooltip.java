package com.wither.betterrod.item;

import com.wither.betterrod.init.ItemComponentsRegister;
import com.wither.betterrod.item.components.BaitComponent;
import com.wither.betterrod.item.components.FishingRodComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber
public class AddTooltip {
    @SubscribeEvent
    public static void modifyTooltip(ItemTooltipEvent event){
        if(event.getToolTip().isEmpty()) return;
        ItemStack itemStack = event.getItemStack();
        FishingRodComponents rodComponents = itemStack.get(ItemComponentsRegister.FISHING_ROD);
        if(rodComponents != null)
            rodComponents.modifyTooltip(event.getToolTip());
        BaitComponent baitComponent = itemStack.get(ItemComponentsRegister.BAIT);
        if(baitComponent != null)
            baitComponent.modifyTooltip(event.getToolTip());
    }
}
