package com.wither.betterrod.entity;

import com.wither.betterrod.init.EntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BlazeHookEntity extends FireproofHookEntity{
    public BlazeHookEntity(Player player, Level level, int luck, int lureSpeed) {super(player, level, luck, lureSpeed);}

    public BlazeHookEntity(EntityType<? extends @NotNull BlazeHookEntity> type, Level level) {super(type, level);}

    @Override
    protected float entityChance() {
        return 0.2f;
    }

    @Override
    protected Entity catchingNetherEntity() {
        return new Blaze(EntityType.BLAZE, this.level());
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return EntityRegister.BLAZE_HOOK.get();
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos center = this.getOnPos();
        BlockPos minPos = center.offset(-1,-1,-1);
        BlockPos maxPos = center.offset(1,1,1);
        for(BlockPos pos : BlockPos.betweenClosed(minPos,maxPos)){
            if(level().getBlockState(pos).is(Blocks.ICE)) {
                level().destroyBlock(pos, true, this.getOwner());
                level().setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
            }
        }
        Entity hookedIn = this.getHookedIn();
        if(hookedIn != null && !hookedIn.isOnFire() && ! hookedIn.isInWaterOrRain()) {
            hookedIn.setRemainingFireTicks(20);
            if(hookedIn instanceof ItemEntity item && this.level() instanceof ServerLevel serverLevel){
                ItemStack itemStack = item.getItem();
                SingleRecipeInput input = new SingleRecipeInput(itemStack);
                Optional<RecipeHolder<@NotNull SmeltingRecipe>> optional = serverLevel.recipeAccess().getRecipeFor(
                        RecipeType.SMELTING,
                        input,
                        serverLevel
                );
                if(optional.isPresent()) {
                    SmeltingRecipe recipe = optional.get().value();
                    ItemStack newItem = recipe.assemble(input);
                    newItem.setCount(itemStack.getCount());
                    item.setItem(newItem);
                }
            }
        }
    }
}
