package com.wither.betterrod.init;

import com.wither.betterrod.BetterRodMod;
import com.wither.betterrod.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class EntityRegister {
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(BetterRodMod.MODID);

    public static final DeferredHolder<@NotNull EntityType<?>, @NotNull EntityType<@NotNull StickyHookEntity>> STICKY_HOOK = ENTITIES.registerEntityType(
            "sticky_hook",
            StickyHookEntity::new,
            MobCategory.MISC,
            (builder -> builder
                    .noLootTable()
                    .noSave()
                    .noSummon()
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(5)
            )
    );


    public static final DeferredHolder<@NotNull EntityType<?>, @NotNull EntityType<@NotNull NetheriteHookEntity>> NETHERITE_HOOK = ENTITIES.registerEntityType(
            "netherite_hook",
            NetheriteHookEntity::new,
            MobCategory.MISC,
            (builder -> builder
                    .noLootTable()
                    .noSave()
                    .noSummon()
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(5)
                    .fireImmune()
            )
    );

    public static final DeferredHolder<@NotNull EntityType<?>, @NotNull EntityType<@NotNull NetherBrickHookEntity>> NETHER_BRICK_HOOK = ENTITIES.registerEntityType(
            "nether_brick_hook",
            NetherBrickHookEntity::new,
            MobCategory.MISC,
            (builder -> builder
                    .noLootTable()
                    .noSave()
                    .noSummon()
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(5)
                    .fireImmune()
            )
    );

    public static final DeferredHolder<@NotNull EntityType<?>, @NotNull EntityType<@NotNull BlazeHookEntity>> BLAZE_HOOK = ENTITIES.registerEntityType(
            "blaze_hook",
            BlazeHookEntity::new,
            MobCategory.MISC,
            (builder -> builder
                    .noLootTable()
                    .noSave()
                    .noSummon()
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(5)
                    .fireImmune()
            )
    );

    public static final DeferredHolder<@NotNull EntityType<?>, @NotNull EntityType<@NotNull HookedBlockEntity>> HOOKED_BLOCK = ENTITIES.registerEntityType(
            "hooked_block",
            HookedBlockEntity::new,
            MobCategory.MISC,
            (stickyHookEntityBuilder -> stickyHookEntityBuilder
                    .noLootTable()
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
            )
    );
}
