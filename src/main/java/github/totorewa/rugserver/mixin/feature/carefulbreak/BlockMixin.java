package github.totorewa.rugserver.mixin.feature.carefulbreak;

import github.totorewa.rugserver.feature.carefulbreak.CarefulBreakManager;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "onBlockBreak", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void onCarefulBreak(
            World world, BlockPos pos, ItemStack item, CallbackInfo ci,
            float f, double d, double e, double g, ItemEntity itemEntity) {
        if (CarefulBreakManager.INSTANCE.isBrokenBlock(pos)) {
            ServerPlayerEntity player = CarefulBreakManager.INSTANCE.getBreakingPlayer();
            itemEntity.setPickupDelay(0);
            itemEntity.setOwner(player.getGameProfile().getName());
            itemEntity.onPlayerCollision(player);
            CarefulBreakManager.INSTANCE.clear();
        }
    }
}
