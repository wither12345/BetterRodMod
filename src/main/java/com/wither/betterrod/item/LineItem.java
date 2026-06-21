package com.wither.betterrod.item;

import com.wither.betterrod.item.components.FishingEquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LineItem extends RodEquipmentItem{
    public LineItem(Properties properties) {
        super(properties);
    }

    @Override
    public FishingEquipmentSlot getSlot() {
        return FishingEquipmentSlot.LINE;
    }

    public static List<Entity> getEntities(Projectile hook, Entity hookedIn, Vec3 vec0, Vec3 vec1){
        AABB area = new AABB(vec0.x, vec0.y, vec0.z, vec1.x, vec1.y, vec1.z);

        return hook.level().getEntities(hook.getOwner(), area,
                entity -> entity.isAlive() && entity instanceof LivingEntity  && entity != hookedIn
        );
    }

    public static boolean lineIntersectsAABB(Vec3 start, Vec3 end, AABB box) {
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double dz = end.z - start.z;

        double tMin = 0;
        double tMax = 1;

        if (dx != 0) {
            double t1 = (box.minX - start.x) / dx;
            double t2 = (box.maxX - start.x) / dx;
            if (t1 > t2) { double temp = t1; t1 = t2; t2 = temp; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
        } else {
            if (start.x < box.minX || start.x > box.maxX) return false;
        }

        if (dy != 0) {
            double t1 = (box.minY - start.y) / dy;
            double t2 = (box.maxY - start.y) / dy;
            if (t1 > t2) { double temp = t1; t1 = t2; t2 = temp; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
        } else {
            if (start.y < box.minY || start.y > box.maxY) return false;

        }

        if (dz != 0) {
            double t1 = (box.minZ - start.z) / dz;
            double t2 = (box.maxZ - start.z) / dz;
            if (t1 > t2) { double temp = t1; t1 = t2; t2 = temp; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
        } else {
            if (start.z < box.minZ || start.z > box.maxZ) return false;
        }

        return tMin <= tMax;
    }
}
