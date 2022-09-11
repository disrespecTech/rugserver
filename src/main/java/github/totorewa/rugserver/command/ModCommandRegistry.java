package github.totorewa.rugserver.command;

import com.google.common.collect.Sets;
import net.minecraft.command.AbstractCommand;

import java.util.Set;

public class ModCommandRegistry {
    private static final Set<String> commandNames = Sets.newHashSet();

    public static void register(AbstractCommand command) {
        commandNames.add(command.getCommandName());
    }

    public static boolean isRegistered(String name) {
        return commandNames.contains(name);
    }
}
