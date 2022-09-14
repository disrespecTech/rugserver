package github.totorewa.rugserver.mixin.feature.player;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    // Filter out player entities to prevent fake players from being pushed.
    // There is no player collision in 1.8 however as the physics of the player
    // are being done by the server, fake players collide like other entities.
    @Redirect(
            method = "tickCramming",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getEntitiesIn(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Lcom/google/common/base/Predicate;)Ljava/util/List;"
            ))
    private List<Entity> preventPlayerCollisions(World world, Entity entity, Box box, Predicate<? super Entity> predicate) {
        return world.getEntitiesIn(entity, box, Predicates.and(predicate, p -> !(p instanceof PlayerEntity)));
    }
}
