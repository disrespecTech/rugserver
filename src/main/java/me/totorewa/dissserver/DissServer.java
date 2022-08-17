package me.totorewa.dissserver;

import com.google.common.collect.Sets;
import me.totorewa.dissserver.command.CommandTps;
import me.totorewa.dissserver.helper.MobcapHelper;
import me.totorewa.dissserver.helper.TPSHelper;
import me.totorewa.dissserver.util.message.Message;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;

import java.util.Set;
import java.util.UUID;

public class DissServer {
    public final MinecraftServer minecraftServer;
    private final Set<UUID> playersTrackingTps = Sets.<UUID>newHashSet();
    private final Set<String> nonOpCommandNames = Sets.<String>newHashSet();
    private final IChatComponent titleComponent;
    private boolean started;

    public DissServer(MinecraftServer server) {
        this.minecraftServer = server;
        this.titleComponent = Message.createComponent(
                new Message("disrespec", Message.RED)
                        .add("Tech ", Message.DARK_RED | Message.BOLD)
                        .add("SMP", Message.RESET));
    }

    // TODO move TPS tracking into generic tracking class
    public void trackTPS(UUID playerUUID) {
        playersTrackingTps.add(playerUUID);
    }

    public void untrackTPS(UUID playerUUID) {
        playersTrackingTps.remove(playerUUID);
    }

    public boolean isTrackingTPS(UUID playerUUID) {
        return playersTrackingTps.contains(playerUUID);
    }

    public boolean isNonOpCommand(String commandName) {
        return this.nonOpCommandNames.contains(commandName);
    }

    public void onServerStart() {
        if (started) return;
        registerCommands((CommandHandler) minecraftServer.getCommandManager());
        started = true;
    }

    public void startTick(long ticks) {
        this.minecraftServer.theProfiler.startSection("diss-start");
        if (ticks % 20 == 0) {
            logTrackers();
        }
        this.minecraftServer.theProfiler.endSection();
    }

    public void endTick(long ticks) {
        this.minecraftServer.theProfiler.startSection("diss-end");
        this.minecraftServer.theProfiler.endSection();
    }

    private void registerCommands(CommandHandler registry) {
        registry.registerCommand(new CommandTps());
        nonOpCommandNames.add("tps");
    }

    private void logTrackers() {
        // TODO rewrite this. This is just a quick and dirty proof-of-concept.
        Message tps = playersTrackingTps.isEmpty() ? null : TPSHelper.createMessage(minecraftServer).add("\n");
        Message[] mobcaps = MobcapHelper.createMessages(minecraftServer);

        for (int i = 0; i < minecraftServer.worldServers.length; i++) {
            WorldServer world = minecraftServer.worldServers[i];
            for (EntityPlayer player : world.playerEntities) {
                EntityPlayerMP mpPlayer = (EntityPlayerMP)player;
                Message footer = new Message();
                if (playersTrackingTps.contains(player.getUniqueID()))
                    footer.add(tps);
                footer.add(mobcaps[i]); // TODO tracker
                // Client seems to disconnect with a NullPointerException if I don't send a header and an empty header
                // creates whitespace in the top of the tab list, so have chosen to put the server name in the header.
                mpPlayer.playerNetServerHandler.sendPacket(
                        S47PacketPlayerListHeaderFooter.create(titleComponent, Message.createComponent(footer)));
            }
        }
    }
}
