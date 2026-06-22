package com.wither.betterrod.item;

import com.wither.betterrod.recipe.AutoBucketRecipe;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class AutoFillerItem extends AccessoryItem{
    public AutoFillerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onPull(Projectile hook, Entity entity) {
        if(entity instanceof Bucketable bucketable && hook.getOwner() instanceof Player player){
            Inventory inventory = player.getInventory();

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);

                if (stack.is(Items.WATER_BUCKET)) {
                    entity.playSound(bucketable.getPickupSound(), 1.0F, 1.0F);
                    ItemStack bucket = bucketable.getBucketItemStack();
                    bucketable.saveToBucketTag(bucket);
                    ItemStack result = ItemUtils.createFilledResult(stack, player, bucket, false);
                    inventory.setItem(i , result);
                    Level level = entity.level();
                    if (!level.isClientSide()) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, bucket);
                    }

                    entity.discard();
                    break;
                }
            }
        }
    }

    @Override
    public void modifyLoot(ObjectArrayList<ItemStack> originalLoot, Projectile hook, ServerLevel level) {
        originalLoot.replaceAll(stack -> AutoBucketRecipe.getFromRecipe(stack, hook.getOwner(), level));
    }
}
