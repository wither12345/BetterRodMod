package com.wither.betterrod.init;

import com.wither.betterrod.BetterRodMod;
import com.wither.betterrod.recipe.AutoBucketRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class RecipeRegister {
    public static final DeferredRegister<@NotNull RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, BetterRodMod.MODID);
    public static final DeferredRegister<@NotNull RecipeBookCategory> RECIPE_CATEGORIES =
            DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, BetterRodMod.MODID);
    public static final DeferredRegister<@NotNull RecipeSerializer<?>> RECIPE_SERIALIZER =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, BetterRodMod.MODID);



    public static final Supplier<RecipeType<@NotNull AutoBucketRecipe>> AUTO_BUCKET_TYPE = RECIPE_TYPES.register("auto_bucket", RecipeType::simple);
    public static final Supplier<RecipeBookCategory> AUTO_BUCKET_CATEGORY = RECIPE_CATEGORIES.register("auto_bucket", RecipeBookCategory::new);
    public static final Supplier<RecipeSerializer<@NotNull AutoBucketRecipe>> AUTO_BUCKET_SERIALIZER =
            RECIPE_SERIALIZER.register("auto_bucket",
                    () -> new RecipeSerializer<>(AutoBucketRecipe.MAP_CODEC, AutoBucketRecipe.STREAM_CODEC));
}
