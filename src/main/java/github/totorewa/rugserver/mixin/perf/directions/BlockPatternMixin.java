package github.totorewa.rugserver.mixin.perf.directions;

import github.totorewa.rugserver.helper.QuickDirections;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockPattern.class)
public class BlockPatternMixin {
    @Redirect(
            method = "method_9043",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] perf$method_9043Directions() {
        return QuickDirections.DIRECTIONS;
    }
}
