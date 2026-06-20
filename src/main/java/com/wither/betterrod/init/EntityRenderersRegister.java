package com.wither.betterrod.init;

import com.wither.betterrod.client.HookEntityRenderer;
import com.wither.betterrod.client.HookedBlockRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class EntityRenderersRegister {
    private static final Identifier STICKY = Identifier.parse("better_rod:textures/entity/sticky_hook.png");
    private static final Identifier NETHERITE = Identifier.parse("better_rod:textures/entity/netherite_hook.png");
    private static final Identifier NETHER_BRICK = Identifier.parse("better_rod:textures/entity/nether_brick_hook.png");

    @SubscribeEvent
    public static void onRenderEvent(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(EntityRegister.STICKY_HOOK.get(), context -> new HookEntityRenderer(context, STICKY));
        event.registerEntityRenderer(EntityRegister.NETHERITE_HOOK.get(), context -> new HookEntityRenderer(context, NETHERITE));
        event.registerEntityRenderer(EntityRegister.NETHER_BRICK_HOOK.get(), context -> new HookEntityRenderer(context, NETHER_BRICK));
        event.registerEntityRenderer(EntityRegister.HOOKED_BLOCK.get(), HookedBlockRenderer::new);
    }
}
