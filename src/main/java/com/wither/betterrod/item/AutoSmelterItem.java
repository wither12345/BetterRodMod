package com.wither.betterrod.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AutoSmelterItem extends AccessoryItem{
    public AutoSmelterItem(Properties properties) {
        super(properties);
    }

    @Override
    public void modifyLoot(ObjectArrayList<ItemStack> originalLoot, Projectile hook, ServerLevel level) {
        originalLoot.replaceAll(stack -> getFromSmeltingRecipe(stack, level));
    }

    public static ItemStack getFromSmeltingRecipe(ItemStack itemStack, ServerLevel serverLevel){
        SingleRecipeInput input = new SingleRecipeInput(itemStack);
        Optional<RecipeHolder<@NotNull SmeltingRecipe>> optional = serverLevel.recipeAccess().getRecipeFor(
                RecipeType.SMELTING,
                input,
                serverLevel
        );
        if(optional.isEmpty())
            return itemStack;
        SmeltingRecipe recipe = optional.get().value();
        return recipe.assemble(input);
    }
}
