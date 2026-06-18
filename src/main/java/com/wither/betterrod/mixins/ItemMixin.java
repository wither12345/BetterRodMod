package com.wither.betterrod.mixins;

import com.wither.betterrod.init.ItemComponentsRegister;
import com.wither.betterrod.item.RodEquipmentItem;
import com.wither.betterrod.item.components.FishingRodComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "overrideStackedOnOther",
        at = @At("HEAD"),
        cancellable = true
    )
    public void overrideStackedOnOther(ItemStack self, Slot slot, ClickAction clickAction, Player player, CallbackInfoReturnable<Boolean> info){
        FishingRodComponents fishingRodComponents = self.get(ItemComponentsRegister.FISHING_ROD);
        FishingRodComponents.FishingEquipmentSlot equipmentSlot;
        if (fishingRodComponents != null && self.getCount() == 1) {
            ItemStack other = slot.getItem();
            if (other.getItem() instanceof RodEquipmentItem equipmentItem) {
                equipmentSlot = equipmentItem.getSlot();
                if (clickAction == ClickAction.PRIMARY && !other.isEmpty()) {
                    ItemStack preItem = fishingRodComponents.getEquipment(equipmentSlot);
                    slot.set(preItem);
                    self.set(ItemComponentsRegister.FISHING_ROD, fishingRodComponents.replace(equipmentSlot, other));
                    this.better_rod$broadcastChangesOnContainerMenu(player);
                    info.setReturnValue(true);
                }
            }
        }
    }

    @Unique
    private void better_rod$broadcastChangesOnContainerMenu(Player player) {
        AbstractContainerMenu containerMenu = player.containerMenu;
        containerMenu.slotsChanged(player.getInventory());
    }
}
