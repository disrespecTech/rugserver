package github.totorewa.rugserver.mixin.feature.fill;

import github.totorewa.rugserver.RugServerMod;
import net.minecraft.block.Block;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SetBlockCommand.class)
public class SetBlockCommandMixin {
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 2))
    private int overrideFlags(int flags) {
        return flags | (RugServerMod.mod.creativeEnabled ? 128 : 0);
    }

    @Redirect(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;method_8531(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"))
    private void suppressNeighborUpdates(World world, BlockPos pos, Block block) {
        if (!RugServerMod.mod.creativeEnabled)
            world.method_8531(pos, block);
    }
}
