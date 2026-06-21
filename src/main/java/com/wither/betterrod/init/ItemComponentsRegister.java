package com.wither.betterrod.init;

import com.wither.betterrod.BetterRodMod;
import com.wither.betterrod.item.components.BaitComponent;
import com.wither.betterrod.item.components.FishingRodComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ItemComponentsRegister {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, BetterRodMod.MODID);

    public static final Supplier<DataComponentType<@NotNull FishingRodComponents>> FISHING_ROD = REGISTRAR.registerComponentType(
            "fishing_rod",
            builder -> builder
                    .persistent(FishingRodComponents.CODEC)
                    .networkSynchronized(FishingRodComponents.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<@NotNull BaitComponent>> BAIT = REGISTRAR.registerComponentType(
            "bait",
            builder -> builder
                    .persistent(BaitComponent.CODEC)
                    .networkSynchronized(BaitComponent.UNIT_STREAM_CODEC)
    );
}
