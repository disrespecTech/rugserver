package github.totorewa.rugserver.mixin.feature.player;

import github.totorewa.rugserver.feature.player.FakeServerPlayNetworkHandler;
import github.totorewa.rugserver.feature.player.FakeServerPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/server/PlayerManager;loadPlayerData(Lnet/minecraft/entity/player/ServerPlayerEntity;)Lnet/minecraft/nbt/NbtCompound;",
                    shift = At.Shift.AFTER))
    private void correctFakePlayerPos(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (player instanceof FakeServerPlayerEntity) {
            ((FakeServerPlayerEntity) player).moveToStartingPosition();
        }
    }

    @Redirect(
            method = "onPlayerConnect",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/server/network/ServerPlayNetworkHandler"
            ))
    private ServerPlayNetworkHandler correctFakePlayerNetworkHandler(
            MinecraftServer server, ClientConnection connection, ServerPlayerEntity player) {
        return player instanceof FakeServerPlayerEntity
                ? new FakeServerPlayNetworkHandler(server, connection, player)
                : new ServerPlayNetworkHandler(server, connection, player);
    }
}
