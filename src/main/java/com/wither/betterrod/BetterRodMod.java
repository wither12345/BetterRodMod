package com.wither.betterrod;

import com.mojang.logging.LogUtils;
import com.wither.betterrod.init.EntityRegister;
import com.wither.betterrod.init.ItemComponentsRegister;
import com.wither.betterrod.init.ItemRegister;
import com.wither.betterrod.init.RecipeRegister;
import com.wither.betterrod.item.TippedHook;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(BetterRodMod.MODID)
public class BetterRodMod {
    public static final String MODID = "better_rod";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "MODID" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "MODID" namespace
    public static final DeferredRegister<@NotNull CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    /*
    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredBlock<@NotNull Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", p -> p.mapColor(MapColor.STONE));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredItem<@NotNull BlockItem> EXAMPLE_BLOCK_ITEM = ItemRegister.ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

     */
    public static final DeferredHolder<@NotNull CreativeModeTab, @NotNull CreativeModeTab> BETTER_ROD_TAB = CREATIVE_MODE_TABS.register("better_rods", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.better_rods"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ItemRegister.IRON_SPIKED_HOOK.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ItemRegister.SALMON_BAIT);
                output.accept(ItemRegister.COD_BAIT);
                output.accept(ItemRegister.MIXED_BAIT);
                output.accept(ItemRegister.GOLDEN_SPIKED_HOOK);
                output.accept(ItemRegister.IRON_SPIKED_HOOK);
                output.accept(ItemRegister.DIAMOND_SPIKED_HOOK);
                output.accept(ItemRegister.NETHERITE_SPIKED_HOOK);
                output.accept(ItemRegister.NETHER_BRICK_HOOK);
                output.accept(ItemRegister.BLAZE_HOOK);
                output.accept(ItemRegister.STICKY_HOOK);
                output.accept(ItemRegister.WIND_HOOK);
                output.accept(ItemRegister.ENDER_LINE);
                output.accept(ItemRegister.ELASTIC_LINE);
                output.accept(ItemRegister.TWISTING_VINES_LINE);
                output.accept(ItemRegister.WEEPING_VINES_LINE);
                output.accept(ItemRegister.AUTO_FILLER);
                output.accept(ItemRegister.AUTO_SMELTER);
                output.accept(ItemRegister.QUICK_CLOCK);
                output.accept(ItemRegister.LUCK_CLOVER);
                TippedHook.putOnTab(output);
                output.accept(ItemRegister.SILMON);
            }).build());


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public BetterRodMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        EntityRegister.ENTITIES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ItemRegister.ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        ItemComponentsRegister.REGISTRAR.register(modEventBus);
        RecipeRegister.RECIPE_TYPES.register(modEventBus);
        RecipeRegister.RECIPE_CATEGORIES.register(modEventBus);
        RecipeRegister.RECIPE_SERIALIZER.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (BetterRodMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        //modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        /*
        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }
        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
        }
         */
    }

    /*
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

     */

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
