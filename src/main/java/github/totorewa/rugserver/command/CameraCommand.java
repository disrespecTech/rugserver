package github.totorewa.rugserver.command;

import github.totorewa.rugserver.fake.PlayerCameraHandler;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.ServerPlayerEntity;

public class CameraCommand extends AbstractCommand {
    public CameraCommand() {
        ModCommandRegistry.register(this);
    }

    @Override
    public String getCommandName() {
        return "cs";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "cs";
    }

    @Override
    public void execute(CommandSource source, String[] args) throws CommandException {
        if (!(source.getEntity() instanceof ServerPlayerEntity))
            throw new PlayerNotFoundException();
        ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
        PlayerCameraHandler cameraHandler = (PlayerCameraHandler) player;
        String reason = cameraHandler.cameraModeBlockReason();
        if (reason != null) {
            player.sendMessage(new Message("Could not change camera mode: ", Message.GRAY)
                    .add(reason, Message.ITALIC).toText());
        } else if (!(cameraHandler.isInCameraMode() ? cameraHandler.exitCameraMode() : cameraHandler.enterCameraMode())) {
            player.sendMessage(new Message("Could not change camera mode: ", Message.GRAY)
                    .add("unknown", Message.ITALIC).toText());
        } else {
            player.sendMessage(new Message(cameraHandler.isInCameraMode() ? "Entering " : "Exiting ", Message.AQUA)
            .add("camera mode", Message.RESET).toText());
        }
    }
}
