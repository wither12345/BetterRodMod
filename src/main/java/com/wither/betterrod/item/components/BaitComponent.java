package com.wither.betterrod.item.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wither.betterrod.entity.AbstractFishInterface;
import com.wither.betterrod.init.ItemComponentsRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public record BaitComponent(List<Attract> attracts, int ticking_rate, int attract_range, int min_tick) implements ModifyDefaultComponentsEvent.Initializer{
    public static TagKey<@NotNull EntityType<?>> FISH_BAIT = TagKey.create(Registries.ENTITY_TYPE, Identifier.parse("fish_bait"));
    public static TagKey<@NotNull EntityType<?>> INSECT_BAIT = TagKey.create(Registries.ENTITY_TYPE, Identifier.parse("insect_bait"));


    public static final Codec<BaitComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Attract.CODEC.listOf().fieldOf("attracts").forGetter(BaitComponent::attracts),
                    Codec.INT.fieldOf("ticking_rate").forGetter(BaitComponent::ticking_rate),
                    Codec.INT.fieldOf("attract_range").forGetter(BaitComponent::attract_range),
                    Codec.INT.fieldOf("min_tick").forGetter(BaitComponent::min_tick)
            ).apply(instance, BaitComponent::new)
    );
    public static final StreamCodec<@NotNull ByteBuf, @NotNull BaitComponent> UNIT_STREAM_CODEC = StreamCodec.unit(new BaitComponent(null, 0, 0, 500));

    public double getAttractChance(Entity entity){
        for(Attract attract : attracts){
            if(attract.test(entity))
                return attract.attract_rate();
        }
        return 0;
    }

    public static void attracting(Entity attractor, ItemStack bait){
        Level level = attractor.level();
        BlockPos pos = attractor.getOnPos();
        BaitComponent component = bait.get(ItemComponentsRegister.BAIT);
        if(component != null && component.ticking_rate > 0 && level.getGameTime() % component.ticking_rate == 0) {
            int range = component.attract_range();
            Predicate<Entity> entitySelector = EntitySelector.LIVING_ENTITY_STILL_ALIVE.and(entity -> entity instanceof AbstractFish);
            List<Entity> animals = level.getEntities(attractor, new AABB(pos).inflate(range), entitySelector);
            animals.forEach(
                    entity -> {
                        if (entity instanceof AbstractFishInterface fishInterface && component.getAttractChance(entity) > Math.random()) {
                            fishInterface.better_rod$setAttracted(attractor);
                        }
                    }
            );
        }
    }

    @Override
    public void run(DataComponentMap.Builder components, HolderLookup.@NotNull Provider context, @NotNull Item item) {
        components.set(ItemComponentsRegister.BAIT, new BaitComponent(this.attracts, this.ticking_rate, this.attract_range, this.min_tick));
    }

    public record Attract(@NotNull TagKey<@NotNull EntityType<?>> tagKey, double attract_rate){
        public static final Codec<Attract> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        TagKey.codec(Registries.ENTITY_TYPE).fieldOf("types").forGetter(Attract::tagKey),
                        Codec.DOUBLE.fieldOf("attract_rate").forGetter(Attract::attract_rate)
                ).apply(instance, Attract::new)
        );

        public boolean test(Entity entity){
            return entity.is(tagKey);
        }
    }
}
