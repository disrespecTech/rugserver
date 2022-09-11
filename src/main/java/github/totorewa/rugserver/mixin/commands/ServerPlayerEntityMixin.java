package github.totorewa.rugserver.mixin.commands;

import github.totorewa.rugserver.command.ModCommandRegistry;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "canUseCommand", at = @At("HEAD"), cancellable = true)
    private void overrideCanUseCommand(int permissionLevel, String commandLiteral, CallbackInfoReturnable<Boolean> cir) {
        if (ModCommandRegistry.isRegistered(commandLiteral))
            cir.setReturnValue(true);
    }
}
