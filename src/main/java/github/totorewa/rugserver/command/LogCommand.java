package github.totorewa.rugserver.command;

import github.totorewa.rugserver.logging.InfoLogger;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.SyntaxException;
import net.minecraft.entity.player.PlayerEntity;

public class LogCommand extends AbstractCommand {
    public LogCommand() {
        ModCommandRegistry.register(this);
    }

    @Override
    public String getCommandName() {
        return "log";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "log";
    }

    @Override
    public void execute(CommandSource source, String[] args) throws CommandException {
        InfoLogger logger = InfoLogger.loggers.get(args[0]);
        if (logger == null) throw new SyntaxException();
        if (source.getEntity() instanceof PlayerEntity) {
            boolean enabled = logger.toggleLogging(((PlayerEntity)source).getGameProfile().getName());
            source.sendMessage(new Message("You have ", Message.GRAY | Message.ITALIC)
            .add(enabled ? "subscribed" : "unsubscribed", Message.BOLD)
                    .add(" to ", Message.GRAY | Message.ITALIC)
                    .add(logger.name, Message.BOLD).toText());
        }
    }
}