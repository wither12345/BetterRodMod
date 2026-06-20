package com.wither.betterrod.item;

import com.wither.betterrod.init.ItemComponentsRegister;
import com.wither.betterrod.item.components.FishingRodComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;

@EventBusSubscriber
public class onItemEvent {
    @SubscribeEvent
    public static void modifyAttribute(ItemAttributeModifierEvent event){
        FishingRodComponents fishingRodComponents = event.getItemStack().get(ItemComponentsRegister.FISHING_ROD);
        if(fishingRodComponents != null){
            for(FishingEquipmentSlot slot : FishingEquipmentSlot.values()){
                ItemStack stack = fishingRodComponents.getEquipment(slot);
                if(stack.getItem() instanceof AttributeEquipment equipment)
                    equipment.modifyAttribute(event);
            }
        }
    }

    @SubscribeEvent
    public static void onStacked(ItemStackedOnOtherEvent event){
        ItemStack self = event.getCarriedItem();
        Player player = event.getPlayer();
        Slot slot = event.getSlot();
        ClickAction clickAction = event.getClickAction();

        FishingRodComponents fishingRodComponents = self.get(ItemComponentsRegister.FISHING_ROD);
        FishingEquipmentSlot equipmentSlot;
        if (fishingRodComponents != null && self.getCount() == 1 && clickAction == ClickAction.SECONDARY) {
            ItemStack other = slot.getItem();
            if (other.isEmpty()){
                self.set(ItemComponentsRegister.FISHING_ROD, fishingRodComponents.pickUp(slot));
                event.setCanceled(true);
            }
            else if (other.getItem() instanceof RodEquipmentItem equipmentItem) {
                equipmentSlot = equipmentItem.getSlot();
                ItemStack preItem = fishingRodComponents.getEquipment(equipmentSlot);
                slot.set(preItem);
                self.set(ItemComponentsRegister.FISHING_ROD, fishingRodComponents.replace(equipmentSlot, other));
                better_rod$broadcastChangesOnContainerMenu(player);
                event.setCanceled(true);
            }
        }
    }

    private static void better_rod$broadcastChangesOnContainerMenu(Player player) {
        AbstractContainerMenu containerMenu = player.containerMenu;
        containerMenu.slotsChanged(player.getInventory());
    }
}
