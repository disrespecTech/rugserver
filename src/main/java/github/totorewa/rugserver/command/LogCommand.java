package github.totorewa.rugserver.command;

import github.totorewa.rugserver.logging.InfoLogger;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.command.*;
import net.minecraft.entity.player.PlayerEntity;

public class LogCommand extends AbstractRugCommand {
    @Override
    public String getCommandName() {
        return "log";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "/log <name>";
    }

    @Override
    public void execute(CommandSource source, String[] args) throws CommandException {
        if (args.length == 0) throw new IncorrectUsageException(getUsageTranslationKey(source));
        InfoLogger logger = InfoLogger.getLogger(args[0]);
        if (logger == null) throw new IncorrectUsageException(getUsageTranslationKey(source));
        if (source.getEntity() instanceof PlayerEntity) {
            boolean enabled = logger.toggleLogging(((PlayerEntity) source).getGameProfile().getName());
            source.sendMessage(new Message("You have ", Message.GRAY)
                    .add(enabled ? "subscribed" : "unsubscribed", Message.ITALIC)
                    .add(" to ", Message.RESET | Message.GRAY)
                    .add(logger.name, Message.ITALIC).toText());
            AbstractCommand.run(source, this, 1,String.format("%s %s", enabled ? "Subscribed to" : "Unsubscribed from", logger.name));
        }
    }
}
