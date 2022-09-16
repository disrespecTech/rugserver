package github.totorewa.rugserver.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandSource;

public abstract class AbstractRugCommand extends AbstractCommand {
    @Override
    public boolean isAccessible(CommandSource source) {
        return isEnabled(source) || super.isAccessible(source);
    }

    protected boolean isEnabled(CommandSource source) {
        return true;
    }
}
