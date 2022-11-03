package github.totorewa.rugserver.mixin.feature.crossdimtp;

import github.totorewa.rugserver.RugSettings;
import github.totorewa.rugserver.helper.Teleporter;
import net.minecraft.command.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.command.TeleportCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TeleportCommand.class)
public class TeleportCommandMixin {
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(
            method = "execute",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/server/command/TeleportCommand;getEntity(Lnet/minecraft/command/CommandSource;Ljava/lang/String;)Lnet/minecraft/entity/Entity;",
                    ordinal = 1,
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true)
    private void teleportAcrossDimensions(CommandSource source, String[] args, CallbackInfo ci, int i, Entity entity, Entity target) {
        if (RugSettings.crossDimensionalTeleporting && entity instanceof ServerPlayerEntity && entity.dimension != target.dimension) {
            Teleporter.teleportPlayer((ServerPlayerEntity) entity, target.x, target.y, target.z, target.dimension, entity.yaw, entity.pitch);
            TeleportCommand.run(source, (Command)this, "commands.tp.success", entity.getTranslationKey(), target.getTranslationKey());
            ci.cancel();
        }
    }
}
