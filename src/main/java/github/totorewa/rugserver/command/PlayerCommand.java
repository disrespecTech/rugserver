package github.totorewa.rugserver.command;

import com.google.common.collect.Lists;
import github.totorewa.rugserver.RugSettings;
import github.totorewa.rugserver.feature.player.FakePlayerManager;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class PlayerCommand extends AbstractRugCommand {
    @Override
    public String getCommandName() {
        return "player";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "/player <username> <spawn/kill/shadow>";
    }

    @Override
    protected boolean isEnabled(CommandSource source) {
        return RugSettings.commandPlayer.isEnabled(source);
    }

    @Override
    public void execute(CommandSource source, String[] args) throws CommandException {
        if (args.length <= 1 || args[0].isEmpty()) throw new IncorrectUsageException(getUsageTranslationKey(source));

        String username = args[0];
        String action = args[1];

        if (action.equals("spawn")) {
            handleSpawn(username, source);
        } else if (action.equals("kill")) {
            handleKill(username, source);
        } else if (action.equals("shadow")) {
            handleShadow(username, source);
        }
    }

    @Override
    public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
        List<String> hints = Lists.newArrayList();
        World world = source.getWorld();
        if (!(world instanceof ServerWorld)) return hints;

        MinecraftServer server = ((ServerWorld)source.getWorld()).getServer();
        String current = args[args.length - 1].toLowerCase();
        if (args.length == 1) {
            hints.add("Alex");
            hints.add("Steve");
            hints.addAll(Arrays.asList(server.getPlayerManager().getPlayerNames()));
        } else if (args.length == 2) {
            hints.add("kill");
            hints.add("shadow");
            hints.add("spawn");
        }
        hints.removeIf(h -> !h.toLowerCase().startsWith(current));
        return hints;
    }

    private void handleSpawn(String username, CommandSource source) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        if (player.server.getPlayerManager().getPlayer(username) != null)
            source.sendMessage(new Message("You can not spawn a player who is already online", Message.RED).toText());
        else if (!FakePlayerManager.getInstance(player.server).spawnFake(username, player))
            source.sendMessage(new Message("Failed to spawn player", Message.RED).toText());
        else AbstractCommand.run(source, this, 1,String.format("Spawned player %s", username));
    }

    private void handleKill(String username, CommandSource source) {
        if (!FakePlayerManager.getInstance(((ServerWorld) source.getWorld()).getServer()).killFake(username))
            source.sendMessage(new Message("Failed to kill player", Message.RED).toText());
        else AbstractCommand.run(source, this, 1, String.format("Killed player %s", username));
    }

    private void handleShadow(String username, CommandSource source) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        if (!player.getGameProfile().getName().equalsIgnoreCase(username))
            source.sendMessage(new Message("You can only shadow yourself", Message.RED).toText());
        else if (FakePlayerManager.getInstance(player.server).spawnShadow(player))
            AbstractCommand.run(source, this, 1,String.format("Spawned shadow %s", username));
    }
}
