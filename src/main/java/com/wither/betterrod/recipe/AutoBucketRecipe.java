package com.wither.betterrod.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wither.betterrod.init.RecipeRegister;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AutoBucketRecipe extends SingleItemRecipe {
    public static final MapCodec<AutoBucketRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(AutoBucketRecipe::input),
                            Ingredient.CODEC.fieldOf("bucket").forGetter(AutoBucketRecipe::bucket),
                            ItemStackTemplate.CODEC.fieldOf("result").forGetter(AutoBucketRecipe::result)
                    )
                    .apply(i, AutoBucketRecipe::new)
    );
    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull AutoBucketRecipe> STREAM_CODEC =  StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC,
            o -> o.commonInfo,
            Ingredient.CONTENTS_STREAM_CODEC,
            AutoBucketRecipe::input,
            Ingredient.CONTENTS_STREAM_CODEC,
            AutoBucketRecipe::bucket,
            ItemStackTemplate.STREAM_CODEC,
            AutoBucketRecipe::result,
            AutoBucketRecipe::new
        );
    private final Ingredient bucket;

    public AutoBucketRecipe(CommonInfo commonInfo, Ingredient input, Ingredient bucket, ItemStackTemplate result) {
        super(commonInfo, input, result);
        this.bucket = bucket;
    }

    public boolean testBucket(ItemStack itemStack){
        return bucket.test(itemStack);
    }

    public Ingredient bucket(){
        return bucket;
    }

    @Override
    public @NotNull String group() {
        return "";
    }

    @Override
    public @NotNull RecipeSerializer<@NotNull AutoBucketRecipe> getSerializer() {
        return RecipeRegister.AUTO_BUCKET_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<@NotNull AutoBucketRecipe> getType() {
        return RecipeRegister.AUTO_BUCKET_TYPE.get();
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeRegister.AUTO_BUCKET_CATEGORY.get();
    }

    public static ItemStack getFromRecipe(ItemStack itemStack, Entity owner, ServerLevel serverLevel){
        SingleRecipeInput input = new SingleRecipeInput(itemStack);
        Optional<RecipeHolder<@NotNull AutoBucketRecipe>> optional = serverLevel.recipeAccess().getRecipeFor(
                RecipeRegister.AUTO_BUCKET_TYPE.get(),
                input,
                serverLevel
        );
        if(optional.isEmpty())
            return itemStack;
        AutoBucketRecipe recipe = optional.get().value();
        if(owner instanceof Player player){
            NonNullList<@NotNull ItemStack> stacks = player.getInventory().getNonEquipmentItems();
            for(ItemStack stack : stacks){
                if(recipe.testBucket(stack)) {
                    stack.shrink(1);
                    return recipe.assemble(input);
                }
            }
        }
        return itemStack;
    }
}
