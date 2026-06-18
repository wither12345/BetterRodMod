package com.wither.betterrod.init;

import com.wither.betterrod.network.SwingRodData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class PayloadRegister {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.commonToServer(
                SwingRodData.TYPE,
                SwingRodData.STREAM_CODEC,
                SwingRodData::handle
        );
    }
}
