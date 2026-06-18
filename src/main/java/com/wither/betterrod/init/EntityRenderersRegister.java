package com.wither.betterrod.init;

import com.wither.betterrod.client.HookedBlockRenderer;
import com.wither.betterrod.client.NetheriteHookRenderer;
import com.wither.betterrod.client.StickyHookRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class EntityRenderersRegister {
    @SubscribeEvent
    public static void onRenderEvent(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(EntityRegister.STICKY_HOOK.get(), StickyHookRenderer::new);
        event.registerEntityRenderer(EntityRegister.NETHERITE_HOOK.get(), NetheriteHookRenderer::new);
        event.registerEntityRenderer(EntityRegister.HOOKED_BLOCK.get(), HookedBlockRenderer::new);
    }
}
