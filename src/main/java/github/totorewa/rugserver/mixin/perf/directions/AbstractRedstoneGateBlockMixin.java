package github.totorewa.rugserver.mixin.perf.directions;

import github.totorewa.rugserver.RugServerMod;
import github.totorewa.rugserver.helper.QuickDirections;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractRedstoneGateBlock.class)
public class AbstractRedstoneGateBlockMixin {
    @Redirect(
            method = "neighborUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] perf$neighborUpdateDirections() {
        return QuickDirections.DIRECTIONS;
    }
}
