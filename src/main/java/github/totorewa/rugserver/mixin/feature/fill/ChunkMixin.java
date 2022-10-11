package github.totorewa.rugserver.mixin.feature.fill;

import github.totorewa.rugserver.fake.IFillUpdateState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Chunk.class)
public abstract class ChunkMixin {
    @Shadow public abstract BlockEntity getBlockEntity(BlockPos pos, Chunk.Status status);

    @Redirect(
            method = "getBlockState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;onBreaking(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
    private void suppressBreakingUpdates(Block block, World world, BlockPos pos, BlockState state) {
        if (!((IFillUpdateState)world).suppressNeighborUpdates()) {
            block.onBreaking(world, pos, state);
            return;
        }
        if (!world.isClient) {
            BlockEntity be = getBlockEntity(pos, Chunk.Status.CHECK);
            if (be instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory) be);
            }
        }
        world.removeBlockEntity(pos);
    }

    @Redirect(
            method = "getBlockState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;onCreation(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
    private void suppressCreationUpdates(Block block, World world, BlockPos pos, BlockState state) {
        if (!((IFillUpdateState)world).suppressNeighborUpdates())
            block.onBreaking(world, pos, state);
    }
}
