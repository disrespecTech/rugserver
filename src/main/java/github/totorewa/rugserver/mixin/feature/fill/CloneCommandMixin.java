package github.totorewa.rugserver.mixin.feature.fill;

import github.totorewa.rugserver.RugSettings;
import net.minecraft.block.Block;
import net.minecraft.server.command.CloneCommand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CloneCommand.class)
public class CloneCommandMixin {
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 32768))
    private int overrideFillLimit(int constant) {
        return RugSettings.fillLimit;
    }

    @ModifyConstant(method = "execute", constant = @Constant(intValue = 2))
    private int overrideFlags(int flags) {
        return flags | (RugSettings.fillUpdates ? 0 : 128);
    }

    @ModifyConstant(method = "execute", constant = @Constant(intValue = 3, ordinal = 1))
    private int overrideFlags2(int flags) {
        return flags | (RugSettings.fillUpdates ? 0 : 128);
    }

    @Redirect(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;method_8531(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"))
    private void suppressNeighborUpdates(World world, BlockPos pos, Block block) {
        if (RugSettings.fillUpdates)
            world.method_8531(pos, block);
    }
}
