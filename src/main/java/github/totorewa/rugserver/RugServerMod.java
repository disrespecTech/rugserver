package github.totorewa.rugserver;

import github.totorewa.rugserver.logging.FooterController;
import github.totorewa.rugserver.logging.InfoLogger;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;

public class RugServerMod implements ModInitializer, Tickable {
    public static RugServerMod mod;

    @Override
    public void onInitialize() {
        mod = this;
        MinecraftServer.getServer().addTickable(this);
        InfoLogger.registerLoggers();
    }

    @Override
    public void tick() {
        final MinecraftServer server = MinecraftServer.getServer();
        for (ServerWorld world : server.worlds) {
            for (InfoLogger logger : InfoLogger.loggers.values()) {
                logger.tick(world, server.getTicks());
            }
            FooterController.tick(world, server.getTicks());
        }
    }
}
