package github.totorewa.rugserver.mixin.commands;

import github.totorewa.rugserver.command.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class CommandManagerMixin extends CommandRegistry {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCommands(CallbackInfo ci) {
        registerCommand(new CameraCommand());
        registerCommand(new FlowerCommand());
        registerCommand(new LogCommand());
        registerCommand(new PlayerCommand());
        registerCommand(new RugCommand());
        registerCommand(new TickCommand());
    }
}
