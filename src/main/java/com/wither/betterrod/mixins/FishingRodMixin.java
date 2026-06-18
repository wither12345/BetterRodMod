package com.wither.betterrod.mixins;

import com.wither.betterrod.init.ItemComponentsRegister;
import com.wither.betterrod.item.components.FishingRodComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodItem.class)
public abstract class FishingRodMixin extends Item {
    public FishingRodMixin(Properties properties) {super(properties);}
    @Inject(method = "use",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack_ = player.getItemInHand(hand);

        FishingRodComponents fishingRod = itemStack_.get(ItemComponentsRegister.FISHING_ROD);
        if(player.fishing == null && fishingRod != null) {
            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.FISHING_BOBBER_THROW,
                    SoundSource.NEUTRAL,
                    0.5F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            if (level instanceof ServerLevel serverLevel) {
                int lureSpeed = (int)(EnchantmentHelper.getFishingTimeReduction(serverLevel, itemStack_, player) * 20.0F);
                int luck = EnchantmentHelper.getFishingLuckBonus(serverLevel, itemStack_, player);
                Projectile.spawnProjectile(fishingRod.createHook(player, level, luck, lureSpeed), serverLevel, itemStack_);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            itemStack_.causeUseVibration(player, GameEvent.ITEM_INTERACT_START);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
