package github.totorewa.rugserver.command;

import com.google.common.collect.Lists;
import github.totorewa.rugserver.RugSettings;
import github.totorewa.rugserver.fake.IPlayerControllerAccessor;
import github.totorewa.rugserver.feature.flowerforest.FlowerPainter;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.command.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class FlowerCommand extends AbstractRugCommand {
    @Override
    public String getCommandName() {
        return "flower";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "/flower paint [radius] [username] OR /flower stop [username]";
    }

    @Override
    protected boolean isEnabled(CommandSource source) {
        return RugSettings.commandFlower.isEnabled(source);
    }

    @Override
    public void execute(CommandSource source, String[] args) throws CommandException {
        if (args.length < 1 || args.length > 3)
            throw new IncorrectUsageException(getUsageTranslationKey(source));
        String action = args[0];
        if (action.equals("paint")) {
            int radius = 10;
            if (args.length > 1) radius = parseClampedInt(args[1], 1, 10);
            ServerPlayerEntity player = getPlayer(source, args, 2);
            if (player == null) return;
            ((IPlayerControllerAccessor)player).getPlayerController().addAugmentation(new FlowerPainter(radius));
            AbstractCommand.run(source, this, String.format("Attached flower painter to %s with radius %d", player.getGameProfile().getName(), radius));
        } else if (action.equals("stop")) {
            ServerPlayerEntity player = getPlayer(source, args, 1);
            if (player == null) return;
            ((IPlayerControllerAccessor)player).getPlayerController().removeAugmentationByName(FlowerPainter.NAME);
            AbstractCommand.run(source, this, String.format("Removed flower painter from %s", player.getGameProfile().getName()));
        } else throw new IncorrectUsageException(getUsageTranslationKey(source));
    }

    @Override
    public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
        List<String> hints = Lists.newArrayList();
        String current = args[args.length - 1].toLowerCase();
        if (args.length == 1) {
            hints.add("paint");
            hints.add("stops");
        } else if (args[0].equals("paint")) {
            if (args.length == 2) {
                for (int i = 1; i < 11; i++)
                    hints.add(String.valueOf(i));
            } else if (args.length == 3) {
                World world = source.getWorld();
                if (world instanceof ServerWorld)
                    hints.addAll(Arrays.asList(((ServerWorld) world).getServer().getPlayerManager().getPlayerNames()));
            }
        } else if (args[0].equals("stop") && args.length == 2) {
            World world = source.getWorld();
            if (world instanceof ServerWorld)
                hints.addAll(Arrays.asList(((ServerWorld) world).getServer().getPlayerManager().getPlayerNames()));
        }
        hints.removeIf(h -> !h.toLowerCase().startsWith(current));
        return hints;
    }

    public ServerPlayerEntity getPlayer(CommandSource source, String[] args, int i) throws PlayerNotFoundException, IncorrectUsageException {
        ServerPlayerEntity player;

        if (args.length == i + 1)
            player = AbstractCommand.getPlayer(source, args[i]);
        else
            player = AbstractCommand.getAsPlayer(source);
        if (player == null) throw new IncorrectUsageException(getUsageTranslationKey(source));
        if (!player.interactionManager.getGameMode().isCreative()) {
            source.sendMessage(new Message("Player must be in creative mode").toText());
            return null;
        }
        return player;
    }
}
