package com.wither.betterrod.item.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wither.betterrod.init.ItemComponentsRegister;
import com.wither.betterrod.item.HookItem;
import com.wither.betterrod.item.RodEquipmentItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record FishingRodComponents(ItemStack line, ItemStack hook, ItemStack accessory) implements ModifyDefaultComponentsEvent.Initializer {
    public static final Codec<FishingRodComponents> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.OPTIONAL_CODEC.fieldOf("line").forGetter(FishingRodComponents::line),
                    ItemStack.OPTIONAL_CODEC.fieldOf("hook").forGetter(FishingRodComponents::hook),
                    ItemStack.OPTIONAL_CODEC.fieldOf("accessory").forGetter(FishingRodComponents::accessory)
            ).apply(instance, FishingRodComponents::new)
    );
    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull FishingRodComponents> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, FishingRodComponents::line,
            ItemStack.OPTIONAL_STREAM_CODEC, FishingRodComponents::hook,
            ItemStack.OPTIONAL_STREAM_CODEC, FishingRodComponents::accessory,
            FishingRodComponents::new
    );

    public FishingRodComponents(){
        this(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
    }

    public ItemStack getEquipment(FishingEquipmentSlot slot) {
        return switch (slot) {
            case LINE -> line;
            case HOOK -> hook;
            case ACCESSORY -> accessory;
        };
    }

    public FishingRodComponents replace(FishingEquipmentSlot slot, ItemStack itemStack) {
        return switch (slot) {
            case LINE -> new FishingRodComponents(itemStack, hook, accessory);
            case HOOK -> new FishingRodComponents(line, itemStack, accessory);
            case ACCESSORY -> new FishingRodComponents(line, hook, itemStack);
        };
    }

    public void modifyTooltip(List<Component> components){
        for(FishingEquipmentSlot slot : FishingEquipmentSlot.values()){
            if(this.getEquipment(slot) != null)
                components.add(1,getComponent(this.getEquipment(slot), slot));
        }
        components.add(1,Component.translatable("lore.better_rod.apply"));
    }

    private static Component getComponent(ItemStack itemStack, FishingEquipmentSlot slot){
        if(itemStack.isEmpty()){
            return Component.translatable("lore.better_rod.empty_" + slot.name().toLowerCase());
        }
        return Component.literal("[").append(itemStack.getHoverName()).append("]");
    }

    public FishingHook createHook(Player player, Level level, int luck, int lureSpeed) {
        FishingHook hook =
                (getEquipment(FishingEquipmentSlot.HOOK).getItem() instanceof HookItem item) ?
                item.getHook(player,level,luck,lureSpeed) :
                new FishingHook(player,level,luck,lureSpeed);

        for(FishingEquipmentSlot slot :FishingEquipmentSlot.values())
            if(getEquipment(slot).getItem() instanceof RodEquipmentItem item)
                item.modifyHook(hook, getEquipment((slot)));
        return hook;
    }

    public FishingRodComponents pickUp(Slot slot) {
        if(!line.isEmpty()){
            slot.set(line);
            return new FishingRodComponents(ItemStack.EMPTY, hook, accessory);
        }
        if(!hook.isEmpty()){
            slot.set(hook);
            return new FishingRodComponents(ItemStack.EMPTY, ItemStack.EMPTY, accessory);
        }
        slot.set(accessory);
        return new FishingRodComponents(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public void run(DataComponentMap.Builder components, HolderLookup.@NotNull Provider context, @NotNull Item item) {
        components.set(ItemComponentsRegister.FISHING_ROD, new FishingRodComponents(
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        ));
    }
}
