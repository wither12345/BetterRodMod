package com.wither.betterrod.entity;

import com.wither.betterrod.init.ItemComponentsRegister;
import com.wither.betterrod.item.components.BaitComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class onItemEntityTick {
    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event){
        if(event.getEntity() instanceof ItemEntity itemEntity && itemEntity.isInWater()){
            ItemStack stack = itemEntity.getItem();
            BaitComponent baitComponent = stack.get(ItemComponentsRegister.BAIT);
            if(baitComponent != null && itemEntity.level() instanceof ServerLevel level){
                BaitComponent.attracting(itemEntity, stack);
            }
        }
    }
}
