package github.totorewa.rugserver.helper;

import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.predicate.EntityPredicates;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class RayTraceHelper {
    public static BlockHitResult rayTrace(Entity entity, double maxDistance) {
        Vec3d cameraPos = getCameraPos(entity);
        Vec3d rot = entity.getRotationVector(1);
        Vec3d vec = cameraPos.add(rot.x * maxDistance, rot.y * maxDistance, rot.z * maxDistance);
        BlockHitResult blockHit = entity.world.rayTrace(cameraPos, vec, false, false, true);
        double dist = blockHit == null ? maxDistance : distanceBetween(blockHit.pos, cameraPos);

        List<Entity> entities = entity.world.getEntitiesIn(
                entity,
                entity.getBoundingBox()
                        .stretch(rot.x * maxDistance, rot.y * maxDistance, rot.z * maxDistance)
                        .expand(1, 1, 1),
                Predicates.and(EntityPredicates.EXCEPT_SPECTATOR, Entity::collides));

        Entity hitEntity = null;
        Vec3d hitPos = null;
        double hitDistance;
        double currentDist = dist;
        for (Entity other : entities) {
            float margin = other.getTargetingMargin();
            Box hitbox = other.getBoundingBox().expand(margin, margin, margin);
            BlockHitResult hitResult = hitbox.method_585(cameraPos, vec);
            if (hitbox.contains(cameraPos)) {
                if (!(currentDist >= 0.0)) continue;
                hitEntity = other;
                hitPos = hitResult == null ? cameraPos : hitResult.pos;
                currentDist = 0.0d;
                continue;
            }
            if (hitResult == null || (hitDistance = distanceBetween(cameraPos, hitResult.pos)) >= currentDist && currentDist != 0.0)
                continue;
            if (other == entity.vehicle) {
                if (currentDist != 0.0) continue;
                hitEntity = other;
                hitPos = hitResult.pos;
                continue;
            }
            hitEntity = other;
            hitPos = hitResult.pos;
            currentDist = hitDistance;
        }

        if (hitEntity != null) {
            if (distanceBetween(cameraPos, hitPos) > 3.0)
                return new BlockHitResult(BlockHitResult.Type.MISS, hitPos, null, new BlockPos(hitPos));
            if (currentDist < dist || blockHit == null)
                return new BlockHitResult(hitEntity, hitPos);
        }

        return blockHit;
    }

    public static double distanceBetween(Vec3d source, Vec3d target) {
        double d = target.x - source.x;
        double e = target.y - source.y;
        double f = target.z - source.z;
        return MathHelper.sqrt(d * d + e * e + f * f);
    }

    public static Vec3d getCameraPos(Entity entity) {
        return new Vec3d(entity.x, entity.y + (double)entity.getEyeHeight(), entity.z);
    }
}
