package com.wither.betterrod.item;

import com.wither.betterrod.init.ItemRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

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

    public static void applyEffect(PotionContents contents, LivingEntity victim, @Nullable Entity entity, float durationScale){
        Level var4 = victim.level();
        if (var4 instanceof ServerLevel serverLevel) {
            Player var10000;
            if (entity instanceof Player playerEntity) {
                var10000 = playerEntity;
            } else {
                var10000 = null;
            }

            Player player = var10000;
            contents.forEachEffect((effect) -> {
                if (effect.getEffect().value().isInstantenous()) {
                    effect.getEffect().value().applyInstantenousEffect(serverLevel, player, player, victim, effect.getAmplifier(), (double)1.0F);
                } else {
                    victim.addEffect(effect);
                }

            }, durationScale);
        }
    }
}
