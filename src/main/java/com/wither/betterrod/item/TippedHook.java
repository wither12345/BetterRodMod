package com.wither.betterrod.item;

import com.wither.betterrod.init.ItemRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

public class TippedHook extends HookItem{
    public TippedHook(Properties properties) {
        super(properties);
    }

    public static void putOnTab(CreativeModeTab.Output output){
        BuiltInRegistries.POTION.stream().forEach(
                (pot) -> {
                        ItemStack itemStack = new ItemStack(ItemRegister.TIPPED_HOOK.get());
                        itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Holder.direct(pot)));
                        output.accept(itemStack);
                }
        );
    }
}
