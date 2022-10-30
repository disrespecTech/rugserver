package github.totorewa.rugserver.mixin.perf.directions;

import github.totorewa.rugserver.RugServerMod;
import github.totorewa.rugserver.helper.QuickDirections;
import net.minecraft.block.FireBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    @Redirect(
            method = "method_8778",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] perf$method_8778Directions() {
        return QuickDirections.DIRECTIONS;
    }

    @Redirect(
            method = "method_8779",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] perf$method_8779Directions() {
        return QuickDirections.DIRECTIONS;
    }
}
