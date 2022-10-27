package github.totorewa.rugserver.mixin.feature.noendcredits;

import github.totorewa.rugserver.RugSettings;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Shadow
    @Final
    public MinecraftServer server;

    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Inject(
            method = "teleportToDimension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
                    shift = At.Shift.BEFORE),
            cancellable = true)
    public void preventEndCreditPacket(int dimensionId, CallbackInfo ci) {
        if (RugSettings.noEndCredits) {
            ci.cancel();
            networkHandler.player = server.getPlayerManager().respawnPlayer((ServerPlayerEntity) (Object) this, 0, true);
        }
    }
}
