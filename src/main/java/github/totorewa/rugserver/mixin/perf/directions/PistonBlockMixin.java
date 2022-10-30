package github.totorewa.rugserver.mixin.perf.directions;

import github.totorewa.rugserver.helper.QuickDirections;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBlock.class)
public class PistonBlockMixin {
    @Redirect(
            method = "shouldExtend",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] perf$shouldExtendDirections() {
        return QuickDirections.DIRECTIONS;
    }
}
