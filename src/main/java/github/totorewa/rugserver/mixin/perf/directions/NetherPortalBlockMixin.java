package github.totorewa.rugserver.mixin.perf.directions;

import github.totorewa.rugserver.helper.QuickDirections;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {
    @Redirect(
            method = "findPortal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction$AxisDirection;values()[Lnet/minecraft/util/math/Direction$AxisDirection;"))
    private Direction.AxisDirection[] perf$findPortalDirections() {
        return QuickDirections.AXIS;
    }
}
