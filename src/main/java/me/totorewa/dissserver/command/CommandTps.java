package me.totorewa.dissserver.command;

import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.Locale;

public class CommandTps extends CommandBase {
    public String getCommandName() {
        return "tps";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getCommandUsage(ICommandSender sender) {
        return "commands.generic.notFound"; // TODO change translateable text
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 1)
            throw new WrongUsageException("commands.time.usage");

        MinecraftServer server = ((WorldServer)sender.getEntityWorld()).getMinecraftServer();
        double mspt = MathHelper.average(server.tickTimeArray) * 1.0E-6D;
        double tps = 1000.0D / Math.max(50.0D, mspt);
        sender.addChatMessage(new ChatComponentText(String.format(Locale.US, "%.1f", tps)));
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }
}
