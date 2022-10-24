package github.totorewa.rugserver.mixin.feature.oneblocksuppressor;

import github.totorewa.rugserver.RugSettings;
import github.totorewa.rugserver.fake.IUpdateSuppression;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(World.class)
public class WorldMixin implements IUpdateSuppression {
    @Unique
    private int updateSuppressionTick = -1;

    @Inject(method = "updateNeighborsAlways", at = @At("HEAD"), cancellable = true)
    private void maybeSuppressUpdates(BlockPos pos, Block block, CallbackInfo ci) {
        if (isSuppressionActive())
            ci.cancel();
    }

    @Inject(method = "updateNeighborsExcept", at = @At("HEAD"), cancellable = true)
    private void maybeSuppressUpdates(BlockPos pos, Block block, Direction dir, CallbackInfo ci) {
        if (isSuppressionActive())
            ci.cancel();
    }

    @Inject(
            method = "neighbourUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;neighborUpdate(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/Block;)V",
                    shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    private void trySuppressUpdate(BlockPos pos, Block block, CallbackInfo ci, BlockState blockState) {
        if (shouldSuppressUpdate(blockState.getBlock()))
            ci.cancel();
    }

    private boolean shouldSuppressUpdate(Block block) {
        if (RugSettings.barrierBlockSuppressesUpdates && (Object) this instanceof ServerWorld) {
            int tick = ((ServerWorld) (Object) this).getServer().getTicks();
            if (block == Blocks.BARRIER) {
                updateSuppressionTick = tick;
                return true;
            }
            return updateSuppressionTick == tick;
        }
        return false;
    }

    @Override
    public void suppressTick() {
        if (RugSettings.barrierBlockSuppressesUpdates && (Object) this instanceof ServerWorld) {
            updateSuppressionTick = ((ServerWorld) (Object) this).getServer().getTicks();
        }
    }

    @Override
    public boolean isSuppressionActive() {
        return RugSettings.barrierBlockSuppressesUpdates && (Object) this instanceof ServerWorld &&
                updateSuppressionTick == ((ServerWorld) (Object) this).getServer().getTicks();
    }
}
