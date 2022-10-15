package github.totorewa.rugserver.command;

import github.totorewa.rugserver.fake.IWorldSpawn;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;

public class SpawnChunksCommand extends AbstractCommand {
    public SpawnChunksCommand() {
        ModCommandRegistry.register(this);
    }

    @Override
    public String getCommandName() {
        return "spawnchunks";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "spawnchunks";
    }

    @Override
    public void execute(CommandSource source, String[] args) throws CommandException {
        if (args.length == 0 || args[0].isEmpty()) {
            source.sendMessage(new Message("Spawn chunks is currently ")
                    .add(String.valueOf(((IWorldSpawn) source.getWorld()).getSpawnChunkDistanceInBlocks()), Message.BOLD)
                    .add(" blocks", Message.RESET).toText());
            return;
        }
        int dist = parseClampedInt(args[0], 0, 128);
        ((IWorldSpawn) source.getWorld()).setSpawnChunkDistanceInBlocks(dist);
        source.sendMessage(new Message("Spawn chunks is now ").add(String.valueOf(dist), Message.BOLD).add(" blocks", Message.RESET).toText());
        AbstractCommand.run(source, this, 1, String.format("Spawn chunk size set to %d blocks", dist));
    }
}
