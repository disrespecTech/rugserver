package github.totorewa.rugserver.mixin.perf.directions;

import github.totorewa.rugserver.helper.QuickDirections;
import net.minecraft.block.SpongeBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpongeBlock.class)
public class SpongeBlockMixin {
    @Redirect(
            method = "absorbWater",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] perf$absorbWaterDirections() {
        return QuickDirections.DIRECTIONS;
    }
}
