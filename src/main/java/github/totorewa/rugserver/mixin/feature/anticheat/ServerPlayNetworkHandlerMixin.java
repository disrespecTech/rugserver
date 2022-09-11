package github.totorewa.rugserver.mixin.feature.anticheat;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
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

    @ModifyConstant(method = "onPlayerMove", constant = @Constant(doubleValue = 0.0625))
    private double overrideMovementLimit(double constant) {
        return Double.MAX_VALUE;
    }

    @Redirect(method = "onPlayerMove", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/player/ServerPlayerEntity;noClip:Z",
            opcode = Opcodes.GETFIELD))
    private boolean allowClipping(ServerPlayerEntity instance) {
        return true;
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
