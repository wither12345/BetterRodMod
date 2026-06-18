package com.wither.betterrod.network;

import com.wither.betterrod.BetterRodMod;
import com.wither.betterrod.item.HookInterface;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SwingRodData() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NotNull SwingRodData> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(BetterRodMod.MODID, "swing_rod"));
    public static final StreamCodec<@NotNull ByteBuf, @NotNull SwingRodData> STREAM_CODEC = StreamCodec.unit(new SwingRodData());

    @Override
    public CustomPacketPayload.@NotNull Type<@NotNull SwingRodData> type() {
        return TYPE;
    }

    public static void handle(final SwingRodData data, final IPayloadContext context){
        if(context.player().fishing instanceof HookInterface hookInterface){
            hookInterface.better_rod$swing();
        }
    }
}