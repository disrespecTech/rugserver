package github.totorewa.rugserver.command;

import com.google.common.collect.Lists;
import github.totorewa.rugserver.feature.player.FakePlayerManager;
import github.totorewa.rugserver.helper.TickHelper;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class TickCommand extends AbstractCommand {
    public TickCommand() {
        ModCommandRegistry.register(this);
    }

    @Override
    public String getCommandName() {
        return "tick";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "tick";
    }

    @Override
    public void execute(CommandSource source, String[] args) throws CommandException {
        if (args.length == 0 || args[0].isEmpty()) throw new IncorrectUsageException(getUsageTranslationKey(source));

        String action = args[0];

        if (action.equals("rate")) {
            if (args.length == 1) {
                source.sendMessage(new Message("Current tick rate is ").add(String.valueOf(TickHelper.getCurrentTickRate()), Message.BOLD).toText());
                return;
            }
            int rate = parseClampedInt(args[1], 1);
            Text error = TickHelper.tryAdjustTickRate(rate);
            if (error == null) {
                source.sendMessage(new Message("Setting tick rate to ").add(String.valueOf(TickHelper.getCurrentTickRate()), Message.BOLD).toText());
                AbstractCommand.run(source, this, 1, String.format("Tick rate set to %d", TickHelper.getCurrentTickRate()));
            } else {
                source.sendMessage(error);
            }
        } else {
            throw new IncorrectUsageException(getUsageTranslationKey(source));
        }
    }

    @Override
    public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
        List<String> hints = Lists.newArrayList();
        String current = args[args.length - 1].toLowerCase();
        if (args.length == 1) {
            hints.add("rate");
        }
        hints.removeIf(h -> !h.toLowerCase().startsWith(current));
        return hints;
    }
}
