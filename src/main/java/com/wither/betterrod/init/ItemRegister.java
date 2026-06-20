package com.wither.betterrod.init;

import com.wither.betterrod.BetterRodMod;
import com.wither.betterrod.item.*;
import com.wither.betterrod.item.components.BaitComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemRegister {



    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BetterRodMod.MODID);


    public static final DeferredItem<@NotNull Item> GOLDEN_SPIKED_HOOK = ITEMS.registerItem("golden_spiked_hook", p -> new SpikedHookItem(p, 4));
    public static final DeferredItem<@NotNull Item> IRON_SPIKED_HOOK = ITEMS.registerItem("iron_spiked_hook", p -> new SpikedHookItem(p, 5));
    public static final DeferredItem<@NotNull Item> DIAMOND_SPIKED_HOOK = ITEMS.registerItem("diamond_spiked_hook", p -> new SpikedHookItem(p, 6));
    public static final DeferredItem<@NotNull Item> NETHERITE_SPIKED_HOOK = ITEMS.registerItem("netherite_spiked_hook", p -> new NetheriteSpikedHookItem(p, 7));
    public static final DeferredItem<@NotNull Item> NETHER_BRICK_HOOK = ITEMS.registerItem("nether_brick_hook", NetherBrickHookItem::new);
    public static final DeferredItem<@NotNull Item> STICKY_HOOK = ITEMS.registerItem("sticky_hook", StickyHook::new);
    public static final DeferredItem<@NotNull Item> WIND_HOOK = ITEMS.registerItem("wind_hook", WindHookItem::new);
    public static final DeferredItem<@NotNull Item> TIPPED_HOOK = ITEMS.registerItem("tipped_hook", p -> new TippedHook(p.component(DataComponents.POTION_DURATION_SCALE, 0.25f)));
    public static final DeferredItem<@NotNull Item> ENDER_LINE = ITEMS.registerItem("ender_line", EnderLineItem::new);
    public static final DeferredItem<@NotNull Item> ELASTIC_LINE = ITEMS.registerItem("elastic_line", ElasticLineItem::new);
    public static final DeferredItem<@NotNull Item> TWISTING_VINES_LINE = ITEMS.registerItem("twisting_vines_line", TwistingVinesLineItem::new);
    public static final DeferredItem<@NotNull Item> WEEPING_VINES_LINE = ITEMS.registerItem("weeping_vines_line", WeepingVinesLineItem::new);
    public static final DeferredItem<@NotNull Item> AUTO_FILLER = ITEMS.registerItem("auto_filler", AutoFillerItem::new);
    public static final DeferredItem<@NotNull Item> QUICK_CLOCK = ITEMS.registerItem("quick_clock", QuickClockItem::new);
    public static final DeferredItem<@NotNull Item> LUCK_CLOVER = ITEMS.registerItem("lucky_clover", LuckyCloverItem::new);
    public static final DeferredItem<@NotNull Item> SALMON_BAIT = ITEMS.registerItem("salmon_bait", p -> new Item(p.component(ItemComponentsRegister.BAIT,
            new BaitComponent(List.of(new BaitComponent.Attract(BaitComponent.FISH_BAIT, 0.05)),5, 20, 100))));
    public static final DeferredItem<@NotNull Item> COD_BAIT = ITEMS.registerItem("cod_bait", p -> new Item(p.component(ItemComponentsRegister.BAIT,
            new BaitComponent(List.of(new BaitComponent.Attract(BaitComponent.FISH_BAIT, 0.03)),5, 40, 160))));
    public static final DeferredItem<@NotNull Item> MIXED_BAIT = ITEMS.registerItem("mixed_bait", p -> new Item(p.component(ItemComponentsRegister.BAIT,
            new BaitComponent(List.of(new BaitComponent.Attract(BaitComponent.FISH_BAIT, 0.05)),5, 30, 100))));
    public static final DeferredItem<@NotNull Item> SILMON = ITEMS.registerItem("silmon",p -> new Item(p
            .food(Foods.SALMON, BetterRodConsumables.SILMON)
    ));
}
