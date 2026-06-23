package com.wither.betterrod.init;

import com.wither.betterrod.item.components.BaitComponent;
import com.wither.betterrod.item.components.FishingRodComponents;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

@EventBusSubscriber
public class AddComponents {
    @SubscribeEvent
    public static void modifyComponents(ModifyDefaultComponentsEvent event) {
        event.modify(Items.FISHING_ROD, new FishingRodComponents());
        event.modify(Items.SPIDER_EYE, new BaitComponent(BaitComponent.INSECT_BAIT, 0.05,5, 30, 60));
    }
}
