package com.wither.betterrod.player;

import com.wither.betterrod.network.SwingRodData;
import net.minecraft.world.item.FishingRodItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class onPlayerLeftClick {
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event){
        if(event.getEntity().getMainHandItem().getItem() instanceof FishingRodItem)
            ClientPacketDistributor.sendToServer(new SwingRodData());
    }
}
