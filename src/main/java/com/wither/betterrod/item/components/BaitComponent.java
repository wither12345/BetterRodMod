package com.wither.betterrod.item.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wither.betterrod.Config;
import com.wither.betterrod.entity.AbstractFishInterface;
import com.wither.betterrod.init.ItemComponentsRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;

public record BaitComponent(@NotNull TagKey<@NotNull EntityType<?>> tagKey, double attract_rate, int ticking_rate, int attract_range, int min_tick) implements ModifyDefaultComponentsEvent.Initializer{
    public static TagKey<@NotNull EntityType<?>>
            FISH_BAIT = TagKey.create(Registries.ENTITY_TYPE, Identifier.parse("fish_bait"));
    public static TagKey<@NotNull EntityType<?>>
            INSECT_BAIT = TagKey.create(Registries.ENTITY_TYPE, Identifier.parse("insect_bait"));
    public static TagKey<@NotNull EntityType<?>>
            SEED_BAIT = TagKey.create(Registries.ENTITY_TYPE, Identifier.parse("seed_bait"));


    public static final Codec<BaitComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    TagKey.codec(Registries.ENTITY_TYPE).fieldOf("types").forGetter(BaitComponent::tagKey),
                    Codec.DOUBLE.fieldOf("attract_rate").forGetter(BaitComponent::attract_rate),
                    Codec.INT.fieldOf("ticking_rate").forGetter(BaitComponent::ticking_rate),
                    Codec.INT.fieldOf("attract_range").forGetter(BaitComponent::attract_range),
                    Codec.INT.fieldOf("min_tick").forGetter(BaitComponent::min_tick)
            ).apply(instance, BaitComponent::new)
    );
    public static final StreamCodec<@NotNull ByteBuf, @NotNull BaitComponent> UNIT_STREAM_CODEC = StreamCodec.unit(new BaitComponent(FISH_BAIT, 0, 0, 0, 500));

    public static void attracting(Entity attractor, ItemStack bait){
        Level level = attractor.level();
        BlockPos pos = attractor.getOnPos();
        BaitComponent component = bait.get(ItemComponentsRegister.BAIT);
        if(component != null && component.ticking_rate > 0 && level.getGameTime() % component.ticking_rate == 0) {
            boolean no_found = true;
            int range = component.attract_range();
            Predicate<Entity> entitySelector = EntitySelector.LIVING_ENTITY_STILL_ALIVE.and(entity -> entity instanceof AbstractFish);
            List<Entity> entities = level.getEntities(attractor, new AABB(pos).inflate(range), entitySelector);
            for(Entity entity: entities) {
                if (entity instanceof AbstractFishInterface fishInterface && component.getAttractChance(entity) > Math.random())
                    fishInterface.better_rod$setAttracted(attractor);
                no_found = false ;
            }
            if(no_found && Config.CHUMMING.get()){
                component.spawnEntityAt(level, pos);
            }
        }

    }

    public void modifyTooltip(List<Component> components){
        Iterable<Holder<@NotNull EntityType<?>>> attracts = this.getAttracts();

        StringJoiner joiner = new StringJoiner(", ");
        for (Holder<@NotNull EntityType<?>> holder : attracts) {
            if (holder != null) {
                joiner.add(holder.value().getDescription().getString());
            }
        }
        Component entityNames = Component.literal(joiner.toString());
        MutableComponent toAdd = Component.translatable("lore.better_rod.attract")
                .append(" ")
                .append(entityNames);
        components.add(1, toAdd);
    }

    private void spawnEntityAt(Level level, BlockPos pos){
        int range = this.attract_range;
        List<EntityType<?>> attracts = new ArrayList<>();
        for(Holder<@NotNull EntityType<?>> entityTypeHolder : this.getAttracts())
            attracts.add(entityTypeHolder.value());
        if(attracts.isEmpty())return;
        EntityType<?> type = attracts.get(RandomSource.create().nextInt(attracts.size()));
        for(int i = 0 ; i < 5 ; i ++) {
            int randomX = RandomSource.create().nextInt(range - 10, range);
            if(RandomSource.create().nextBoolean()) randomX *= -1;
            int randomZ = RandomSource.create().nextInt(range - 10, range);
            if(RandomSource.create().nextBoolean()) randomZ *= -1;
            int randomY = RandomSource.create().nextInt(-10, 0);
            BlockPos randomPos = pos.offset(randomX, randomY, randomZ);
            BlockState state = level.getBlockState(randomPos);
            if (level.hasNearbyAlivePlayer(randomPos.getX(), randomPos.getY(), randomPos.getZ(), 15))
                continue;
            if (state.is(Blocks.WATER)) {
                Entity entityToCreate = type.create(level, EntitySpawnReason.NATURAL);
                if (entityToCreate != null) {
                    entityToCreate.setPos(Vec3.atLowerCornerOf(randomPos));
                    level.addFreshEntity(entityToCreate);
                }
            }
        }
    }


    private double getAttractChance(Entity entity){return entity.is(tagKey) ? attract_range : 0 ;}

    private Iterable<Holder<@NotNull EntityType<?>>> getAttracts(){
        return BuiltInRegistries.ENTITY_TYPE.getTagOrEmpty(tagKey);
    }

    @Override
    public void run(DataComponentMap.Builder components, HolderLookup.@NotNull Provider context, @NotNull Item item) {
        components.set(ItemComponentsRegister.BAIT, new BaitComponent(this.tagKey, this.attract_rate, this.ticking_rate, this.attract_range, this.min_tick));
    }
}
