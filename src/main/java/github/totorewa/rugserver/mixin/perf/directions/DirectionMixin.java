package github.totorewa.rugserver.mixin.perf.directions;

import github.totorewa.rugserver.helper.QuickDirections;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(Direction.class)
public class DirectionMixin {
    @Inject(method = "random", at = @At("HEAD"), cancellable = true)
    private static void randomFromCachedArray(Random random, CallbackInfoReturnable<Direction> cir) {
        cir.setReturnValue(QuickDirections.DIRECTIONS[random.nextInt(QuickDirections.DIRECTIONS.length)]);
    }

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private static void quickGet(Direction.AxisDirection direction, Direction.Axis axis, CallbackInfoReturnable<Direction> cir) {
        switch (axis) {
            case X:
                cir.setReturnValue(direction == Direction.AxisDirection.NEGATIVE ? Direction.WEST : Direction.EAST);
                break;
            case Y:
                cir.setReturnValue(direction == Direction.AxisDirection.NEGATIVE ? Direction.DOWN : Direction.UP);
                break;
            case Z:
                cir.setReturnValue(direction == Direction.AxisDirection.NEGATIVE ? Direction.NORTH : Direction.SOUTH);
                break;
        }
    }

}
