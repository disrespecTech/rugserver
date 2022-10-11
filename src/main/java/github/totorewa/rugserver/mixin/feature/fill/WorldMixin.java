package github.totorewa.rugserver.mixin.feature.fill;

import github.totorewa.rugserver.fake.IFillUpdateState;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin implements IFillUpdateState {
    @Unique
    private boolean suppressingNeighborUpdates = false;


    @Inject(
            method = "method_8506",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/Chunk;getBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/block/BlockState;",
                    shift = At.Shift.BEFORE))
    private void setNeighborUpdateSuppressor(BlockPos blockPos, BlockState blockState, int i, CallbackInfoReturnable<Boolean> cir) {
        this.suppressingNeighborUpdates = (i & 128) != 0;
    }

    @Inject(method = "method_8506", at = @At("TAIL"))
    private void unsetNeighborUpdateSuppressor(BlockPos blockPos, BlockState blockState, int i, CallbackInfoReturnable<Boolean> cir) {
        this.suppressingNeighborUpdates = false;
    }

    @Override
    public boolean suppressNeighborUpdates() {
        return suppressingNeighborUpdates;
    }
}
