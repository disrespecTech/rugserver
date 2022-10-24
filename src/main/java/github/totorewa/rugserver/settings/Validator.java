package github.totorewa.rugserver.settings;

import net.minecraft.command.CommandSource;

public interface Validator<E> {
    E validate(CommandSource source, RugRule<E> rule, E parsedValue, String rawValue);
    String errorDescription();
}
