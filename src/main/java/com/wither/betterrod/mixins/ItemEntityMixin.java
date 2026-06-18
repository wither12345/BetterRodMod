package com.wither.betterrod.mixins;

import com.wither.betterrod.entity.ItemEntityInterface;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityInterface {
    public ItemEntityMixin(EntityType<?> type, Level level) {super(type, level);}

    @Unique private boolean from_lava;

    @Inject(method = "fireImmune", at = @At("RETURN"), cancellable = true)
    public void fireImmune(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(cir.getReturnValueZ() || from_lava);
    }

    @Override
    public void better_rod$setFromLava(boolean fromLava) {
        this.from_lava = fromLava;
    }
}
