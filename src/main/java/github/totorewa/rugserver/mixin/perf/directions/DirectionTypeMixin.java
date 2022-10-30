package github.totorewa.rugserver.mixin.perf.directions;

import github.totorewa.rugserver.helper.QuickDirections;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Direction.DirectionType.class)
public class DirectionTypeMixin {
    @Inject(method = "getDirections", at = @At("HEAD"), cancellable = true)
    private void getDirectionsWithoutConstruction(CallbackInfoReturnable<Direction[]> cir) {
        cir.setReturnValue((Direction.DirectionType) (Object) this == Direction.DirectionType.HORIZONTAL
                ? QuickDirections.HORIZONTAL
                : QuickDirections.VERTICAL);
    }
}
