package github.totorewa.rugserver;

import github.totorewa.rugserver.logging.FooterController;
import github.totorewa.rugserver.logging.InfoLogger;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;

import static com.google.common.base.Preconditions.checkNotNull;

public class RugServerMod implements ModInitializer, Tickable {
    public static RugServerMod mod;
    public MinecraftServer server;

    @Override
    public void onInitialize() {
        mod = this;
    }

    @Override
    public void tick() {
        checkNotNull(server);
        for (ServerWorld world : server.worlds) {
            for (InfoLogger logger : InfoLogger.loggers.values()) {
                logger.tick(world, server.getTicks());
            }
            FooterController.tick(world, server.getTicks());
        }
    }

    public void onServerSetup(MinecraftServer server) {
        this.server = server;
        server.addTickable(this);
        InfoLogger.registerLoggers();
    }
}
