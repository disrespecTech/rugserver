package github.totorewa.rugserver.mixin.feature.anticheat;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    private int field_8932;

    @Shadow
    private int messageCooldown;

    @ModifyConstant(method = "onPlayerAction", constant = @Constant(doubleValue = 36.0))
    private double overrideReachLimit(double constant) {
        return Double.MAX_VALUE;
    }

    @ModifyConstant(method = "onPlayerMove", constant = @Constant(doubleValue = 100))
    private double allowMovementToExceedVelocity(double constant) {
        return Double.MAX_VALUE;
    }

    @ModifyConstant(method = "onPlayerMove", constant = @Constant(doubleValue = 0.0625))
    private double overrideMovementWarning(double constant) {
        return Double.MAX_VALUE;
    }

    @Inject(method = "onPlayerMove", at = @At("HEAD"))
    private void relaxMovement(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if (field_8932 > 79) field_8932--;
    }

    @Inject(method = "onChatMessage", at = @At("TAIL"))
    private void relaxChatSpam(ChatMessageC2SPacket packet, CallbackInfo ci) {
        messageCooldown = 0;
    }
}
