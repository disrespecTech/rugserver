package github.totorewa.rugserver.command;

import com.google.common.collect.Lists;
import github.totorewa.rugserver.settings.RugRule;
import github.totorewa.rugserver.settings.SettingsManager;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class RugCommand extends AbstractRugCommand {
    @Override
    public String getCommandName() {
        return "rug";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "rug";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }

    @Override
    protected boolean isEnabled(CommandSource source) {
        if (source instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source;
            if (player.server.getPlayerManager().isOperator(player.getGameProfile())) {
                OperatorEntry operatorEntry = player.server.getPlayerManager().getOpList().get(player.getGameProfile());
                if (operatorEntry != null) {
                    return operatorEntry.getPermissionLevel() >= getPermissionLevel();
                }
                return player.server.getOpPermissionLevel() >= getPermissionLevel();
            }
        }
        return source.canUseCommand(getPermissionLevel(), getCommandName());
    }

    @Override
    public void execute(CommandSource source, String[] args) throws CommandException {
        if (args.length == 0 || args[0].isEmpty()) {
            listSettings(source);
            return;
        }

        String ruleName = args[0];
        if ((args.length == 1 || args[1].isEmpty())) {
            if (ruleName.equals("setDefault"))
                source.sendMessage(new Message(String.format("/%s %s <rule> <value>", getCommandName(), ruleName), Message.RED).toText());
            else
                describeRule(source, ruleName);
            return;
        }

        String value = args[1];
        if (args.length == 2 && SettingsManager.setRule(source, ruleName, value))
            AbstractCommand.run(source, this, String.format("Rule %s updated to %s", ruleName, value));
    }

    @Override
    public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
        List<String> hints = Lists.newArrayList();
        if (args.length == 0) return hints;

        World world = source.getWorld();
        if (!(world instanceof ServerWorld)) return hints;

        String current = args[args.length - 1].toLowerCase();
        String ruleName = args[0];
        if (args.length == 1 || args.length == 2 && ruleName.equals("setDefault")) {
            SettingsManager.getRuleNames().forEach(hints::add);
            if (args.length == 1)
                hints.add("setDefault");
        } else if (args.length == 2) {
            RugRule<?> rule = SettingsManager.getRule(ruleName);
            if (rule != null) {
                hints.addAll(rule.options);
            }
        }

        hints.removeIf(h -> !h.toLowerCase().startsWith(current));
        return hints;
    }

    private void listSettings(CommandSource source) {
        Message message = new Message("Current Rug settings:\n", Message.WHITE | Message.BOLD);
        for (String ruleName : SettingsManager.getRuleNames()) {
            RugRule<?> rule = SettingsManager.getRule(ruleName);
            if (rule == null) continue;
            String value = rule.read();
            if (value != null)
                message.add(String.format("\n%s ", ruleName), Message.RESET | Message.WHITE)
                        .add(String.format("%s", value), Message.UNDERLINE | (rule.defaultValue.equals(rule.current()) ? Message.GRAY | Message.BOLD : Message.YELLOW));
        }
        source.sendMessage(message.toText());
        AbstractCommand.run(source, this, 1, "Current Rug settings");
    }

    private void describeRule(CommandSource source, String ruleName) {
        RugRule<?> rule = SettingsManager.getRule(ruleName);
        if (rule == null) {
            source.sendMessage(new Message(String.format("Unknown rule: %s", ruleName), Message.RED).toText());
            return;
        }

        Message message = new Message("\n");
        message.add(String.format("%s\n", rule.name), Message.WHITE | Message.BOLD);
        message.add(String.format("%s\nTags:", rule.ruleMeta.desc()), Message.RESET | Message.WHITE);
        for (String cat : rule.ruleMeta.categories())
            message.add(String.format(" [%s]", cat), Message.AQUA);

        boolean isDefault = true;
        String value = rule.read();
        if (value != null) {
            message.add("\nCurrent value: ", Message.RESET | Message.WHITE);
            isDefault = rule.current().equals(rule.defaultValue);
            if (isDefault)
                message.add(String.format("%s (default value)", value), Message.DARK_RED | Message.BOLD);
            else
                message.add(String.format("%s (modified value)", value), Message.GREEN | Message.BOLD);

        }

        if (!rule.options.isEmpty()) {
            message.add("\nOptions: ", Message.RESET | Message.WHITE);
            message.add("[", Message.YELLOW);
            for (String option : rule.options) {
                message.add(" ", Message.RESET);
                if (isDefault)
                    message.add(option, Message.GRAY | (option.equals(value) ? Message.BOLD | Message.UNDERLINE : Message.RESET));
                else
                    message.add(option, option.equals(value) ? Message.YELLOW | Message.UNDERLINE : Message.DARK_GREEN | Message.RESET);
            }
            message.add(" ]", Message.RESET | Message.YELLOW);
        }

        source.sendMessage(message.toText());
        AbstractCommand.run(source, this, 1, String.format("Describe Rug setting %s", rule.name));
    }
}
