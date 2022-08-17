package me.totorewa.dissserver.command;

import me.totorewa.dissserver.helper.TPSHelper;
import me.totorewa.dissserver.util.message.Message;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.UUID;

public class CommandTps extends CommandBase {
    public String getCommandName() {
        return "tps";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/tps <track/untrack/toggle>";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        MinecraftServer server = ((WorldServer) sender.getEntityWorld()).getMinecraftServer();
        if (args.length > 1) {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        if (args.length == 0) {
            sender.addChatMessage(Message.createComponent(TPSHelper.createMessage(server)));
            return;
        }

        UUID uuid = sender.getCommandSenderEntity().getUniqueID();
        String action = args[0];
        if ("track".equals(action)) {
            server.dissServer.trackTPS(uuid);
        } else if ("untrack".equals(action)) {
            server.dissServer.untrackTPS(uuid);
        } else if ("toggle".equals(action)) {
            if (server.dissServer.isTrackingTPS(uuid)) server.dissServer.untrackTPS(uuid);
            else server.dissServer.trackTPS(uuid);
        }

        sender.addChatMessage(Message.createComponent(
                new Message("TPS tracking is ", Message.GRAY | Message.ITALIC)
                        .add(server.dissServer.isTrackingTPS(uuid) ? "on" : "off", Message.BOLD)));
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }
}
