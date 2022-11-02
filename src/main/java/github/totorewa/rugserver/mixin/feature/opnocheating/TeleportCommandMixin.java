package github.totorewa.rugserver.mixin.feature.opnocheating;

import github.totorewa.rugserver.RugSettings;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.command.TeleportCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TeleportCommand.class)
public class TeleportCommandMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void checkEntity(CommandSource source, String[] args, CallbackInfo ci) throws EntityNotFoundException, PlayerNotFoundException {
        if (RugSettings.opNoCheating) {
            Entity entity = source.getEntity();
            if (entity instanceof ServerPlayerEntity && ((ServerPlayerEntity) entity).interactionManager.getGameMode().isSurvivalLike()) {
                source.sendMessage(new Message("You can not execute teleport commands in survival mode.", Message.RED).toText());
                ci.cancel();
                return;
            }

            if (args.length == 2 || args.length == 4 || args.length == 6)
                entity = TeleportCommand.getEntity(source, args[0]);
            else
                entity = null;

            if (entity instanceof ServerPlayerEntity && ((ServerPlayerEntity) entity).interactionManager.getGameMode().isSurvivalLike()) {
                source.sendMessage(new Message("You can not teleport a player that is in survival mode.", Message.RED).toText());
                ci.cancel();
            }
        }
    }
}
