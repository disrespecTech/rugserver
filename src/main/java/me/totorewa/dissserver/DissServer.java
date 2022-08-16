package me.totorewa.dissserver;

import me.totorewa.dissserver.command.CommandTps;
import me.totorewa.dissserver.helper.TPSHelper;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;

public class DissServer {
    private final MinecraftServer server;
    private boolean started;

    public DissServer(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public void onServerStart() {
        if (started) return;
        registerCommands((CommandHandler) server.getCommandManager());
        started = true;
    }

    public void startTick(long ticks) {
        this.server.theProfiler.startSection("diss-start");
//        if (ticks % 20 == 0) {
//            TPSHelper.logTPS(server);
//        }
        this.server.theProfiler.endSection();
    }

    public void endTick(long ticks) {
        this.server.theProfiler.startSection("diss-end");
        this.server.theProfiler.endSection();
    }

    private void registerCommands(CommandHandler registry) {
        registry.registerCommand(new CommandTps());
    }
}
