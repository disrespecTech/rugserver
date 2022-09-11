package github.totorewa.rugserver.mixin.feature.carefulbreak;

import github.totorewa.rugserver.feature.carefulbreak.CarefulBreakManager;
import github.totorewa.rugserver.logging.InfoLogger;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "method_10766", at = @At("HEAD"))
    private void beforeBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (InfoLogger.getLogger("carefulBreak").isLogging(player.getGameProfile().getName()) && player.isSneaking())
            CarefulBreakManager.INSTANCE.carefulBreak(player, pos);
    }

    @Inject(method = "method_10766", at = @At("TAIL"))
    private void afterBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        CarefulBreakManager.INSTANCE.clear();
    }
}
