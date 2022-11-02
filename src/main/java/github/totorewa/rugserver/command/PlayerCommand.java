package github.totorewa.rugserver.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import github.totorewa.rugserver.RugSettings;
import github.totorewa.rugserver.fake.IPlayerControllerAccessor;
import github.totorewa.rugserver.feature.player.ActionAttachment;
import github.totorewa.rugserver.feature.player.FakePlayerManager;
import github.totorewa.rugserver.feature.player.PlayerController;
import github.totorewa.rugserver.feature.player.actions.PlayerActionType;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class PlayerCommand extends AbstractRugCommand {
    private static final Set<String> quickActions = Sets.newHashSet("drop", "mount", "dismount", "unsneak", "sneak");

    @Override
    public String getCommandName() {
        return "player";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "/player <username> <spawn/kill/shadow> OR /player <username> <attack> [once/interval/continuous] [ticks]";
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

        boolean executed = false;
        if (action.equals("spawn")) {
            executed = handleSpawn(username, source);
        } else if (action.equals("kill")) {
            executed = handleKill(username, source);
        } else if (action.equals("shadow")) {
            executed = handleShadow(username, source);
        } else if (PlayerActionType.names.contains(action)) {
            executed = handleReplayAction(source, username, PlayerActionType.nameMap.get(action), args, 2);
        }

        if (!executed) {
            if (quickActions.contains(action))
                handleAction(source, username, action, args, 2);
            else throw new IncorrectUsageException(getUsageTranslationKey(source));
        }
    }

    @Override
    public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
        List<String> hints = Lists.newArrayList();
        World world = source.getWorld();
        if (!(world instanceof ServerWorld)) return hints;

        MinecraftServer server = ((ServerWorld) source.getWorld()).getServer();
        String current = args[args.length - 1].toLowerCase();
        if (args.length == 1) {
            hints.add("Alex");
            hints.add("Steve");
            hints.addAll(Arrays.asList(server.getPlayerManager().getPlayerNames()));
        } else if (args.length == 2) {
            hints.add("kill");
            hints.add("shadow");
            hints.add("spawn");
            hints.addAll(quickActions);
            hints.addAll(PlayerActionType.names);
        } else if (args[1].equals("drop")) {
            if (args.length == 3) {
                hints.add("continuous");
                hints.add("interval");
                hints.add("once");
                hints.add("all");
                hints.add("slot");
                hints.add("xp");
            } else if (args[2].equals("slot")) {
                if (args.length == 4) {
                    for (int i = 0; i < 9; i++) {
                        hints.add(String.valueOf(i));
                    }
                } else if (args.length == 5) {
                    hints.add("true");
                    hints.add("false");
                }
            }
        } else if (PlayerActionType.names.contains(args[1])) {
            if (args.length == 3) {
                hints.add("continuous");
                hints.add("interval");
                hints.add("once");
            } else if (args.length == 4 && args[2].equalsIgnoreCase("interval")) {
                hints.add("10");
                hints.add("20");
                hints.add("30");
                hints.add("40");
            }
        }
        hints.removeIf(h -> !h.toLowerCase().startsWith(current));
        Collections.sort(hints);
        return hints;
    }

    private boolean handleSpawn(String username, CommandSource source) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayerEntity)) return true;
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        if (player.server.getPlayerManager().getPlayer(username) != null)
            source.sendMessage(new Message("You can not spawn a player who is already online", Message.RED).toText());
        else if (!FakePlayerManager.getInstance(player.server).spawnFake(username, player))
            source.sendMessage(new Message("Failed to spawn player", Message.RED).toText());
        else AbstractCommand.run(source, this, 1, String.format("Spawned player %s", username));
        return true;
    }

    private boolean handleKill(String username, CommandSource source) {
        if (!FakePlayerManager.getInstance(((ServerWorld) source.getWorld()).getServer()).killFake(username))
            source.sendMessage(new Message("Failed to kill player", Message.RED).toText());
        else AbstractCommand.run(source, this, 1, String.format("Killed player %s", username));
        return true;
    }

    private boolean handleShadow(String username, CommandSource source) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayerEntity)) return true;
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        if (!player.getGameProfile().getName().equalsIgnoreCase(username))
            source.sendMessage(new Message("You can only shadow yourself", Message.RED).toText());
        else if (FakePlayerManager.getInstance(player.server).spawnShadow(player))
            AbstractCommand.run(source, this, 1, String.format("Spawned shadow %s", username));
        return true;
    }

    private boolean handleReplayAction(CommandSource source, String username, PlayerActionType actionType, String[] commandArgs, int i) throws IncorrectUsageException, InvalidNumberException {
        ServerPlayerEntity player = getPlayerByName(source, username);
        if (player == null) {
            source.sendMessage(new Message("Player must be online", Message.RED).toText());
            return true;
        }
        if (!actionType.action.isEnabled()) {
            source.sendMessage(new Message("Action is not enabled. Please update rug rules to enable it.", Message.RED).toText());
            return true;
        }
        PlayerController controller = ((IPlayerControllerAccessor) player).getPlayerController();
        String period;
        if (commandArgs.length <= i || (period = commandArgs[i++]).isEmpty() || period.equalsIgnoreCase("once")) {
            controller.addAttachment(new ActionAttachment(actionType, 1, 1, false));
            AbstractCommand.run(source, this, 1, String.format("Updated %s's %s action", player.getGameProfile().getName(), actionType.actionName));
            return true;
        }

        if (period.equalsIgnoreCase("interval")) {
            String tickStr;
            if (commandArgs.length <= i || (tickStr = commandArgs[i++]).isEmpty()) {
                source.sendMessage(new Message("Interval must be specified", Message.RED).toText());
                return true;
            }
            int ticks = parseClampedInt(tickStr, 2);
            controller.addAttachment(new ActionAttachment(actionType, ticks, -1, false));
            AbstractCommand.run(source, this, 1, String.format("Updated %s's %s action", player.getGameProfile().getName(), actionType.actionName));
            return true;
        }

        if (period.equalsIgnoreCase("continuous")) {
            controller.addAttachment(new ActionAttachment(actionType, 1, -1, true));
            AbstractCommand.run(source, this, 1, String.format("Updated %s's %s action", player.getGameProfile().getName(), actionType.actionName));
            return true;
        }
        return false;
    }

    private void handleAction(CommandSource source, String username, String action, String[] commandArgs, int i) throws IncorrectUsageException, InvalidNumberException {
        ServerPlayerEntity player = getPlayerByName(source, username);
        if (player == null) {
            source.sendMessage(new Message("Player must be online", Message.RED).toText());
            return;
        }
        PlayerController controller = ((IPlayerControllerAccessor) player).getPlayerController();
        if (action.equals("mount")) {
            if (!controller.mountNearbyVehicle())
                source.sendMessage(new Message("Could not find nearby vehicle to mount", Message.RED).toText());
        }
        else if (action.equals("dismount")) {
            if (!controller.dismountVehicle())
                source.sendMessage(new Message("Failed to dismount player. Are they in a vehicle?", Message.RED).toText());
        }
        else if (action.equals("sneak")) controller.sneak();
        else if (action.equals("unsneak")) controller.unsneak();
        else if (action.equals("drop")) {
            if (commandArgs.length <= i) throw new IncorrectUsageException(getUsageTranslationKey(source));
            String what = commandArgs[i++];
            if (what.equals("all"))
                controller.dropAll();
            else if (what.equals("xp")) {
                if (RugSettings.allowXpDumping == RugSettings.BotExperienceDropType.NONE) {
                    Text text = new Message("Experience dropping is disabled.", Message.RED).toText();
                    Text commandShortcut = new Message(" [Enable?]", Message.AQUA).toText();
                    commandShortcut.getStyle()
                            .setClickEvent(
                                    new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rug allowXpDumping limited"))
                            .setHoverEvent(
                                    new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            Message.createComponent("click to enable experience dropping")));
                    source.sendMessage(text.append(commandShortcut));
                    return;
                }
                controller.dumpExperience();
            }
            else if (what.equals("slot")) {
                if (commandArgs.length <= i) {
                    source.sendMessage(new Message("Slot number is required between 0-40", Message.RED).toText());
                    return;
                }
                controller.dropSlot(parseClampedInt(commandArgs[i++], 0, 40), commandArgs.length > i && commandArgs[i++].equals("true"));
            }
        }
        AbstractCommand.run(source, this, 1, String.format("Updated %s's actions", player.getGameProfile().getName()));
    }

    private static MinecraftServer getServer(CommandSource source) {
        World world = source.getWorld();
        return world instanceof ServerWorld ? ((ServerWorld) world).getServer() : null;
    }

    private static ServerPlayerEntity getPlayerByName(CommandSource source, String name) {
        MinecraftServer server = getServer(source);
        return server == null ? null : server.getPlayerManager().getPlayer(name);
    }
}
