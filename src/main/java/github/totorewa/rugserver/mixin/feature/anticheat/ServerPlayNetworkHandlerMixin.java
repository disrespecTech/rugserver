package github.totorewa.rugserver.mixin.feature.anticheat;

import github.totorewa.rugserver.RugSettings;
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
        return RugSettings.antiCheatDisabled ? Double.MAX_VALUE : 36.0d;
    }

    @ModifyConstant(method = "onPlayerMove", constant = @Constant(doubleValue = 100))
    private double allowMovementToExceedVelocity(double constant) {
        return RugSettings.antiCheatDisabled ? Double.MAX_VALUE : 100.0d;
    }

    @ModifyConstant(method = "onPlayerMove", constant = @Constant(doubleValue = 0.0625))
    private double overrideMovementWarning(double constant) {
        return RugSettings.antiCheatDisabled ? Double.MAX_VALUE : 0.0625d;
    }

    @Inject(method = "onPlayerMove", at = @At("HEAD"))
    private void relaxMovement(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if (RugSettings.antiCheatDisabled && field_8932 > 79) field_8932--;
    }

    @Inject(method = "onChatMessage", at = @At("TAIL"))
    private void relaxChatSpam(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (RugSettings.antiCheatDisabled) messageCooldown = 0;
    }
}
