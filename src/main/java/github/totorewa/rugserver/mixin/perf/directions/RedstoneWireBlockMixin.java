package github.totorewa.rugserver.mixin.perf.directions;

import github.totorewa.rugserver.helper.QuickDirections;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {
    @Redirect(
            method = "method_8875",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] perf$method_8875Directions() {
        return QuickDirections.DIRECTIONS;
    }

    @Redirect(
            method = "updateNeighbors",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] perf$updateNeighborsDirections() {
        return QuickDirections.DIRECTIONS;
    }
}
