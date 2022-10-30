package github.totorewa.rugserver;

import github.totorewa.rugserver.logging.FooterController;
import github.totorewa.rugserver.logging.InfoLogger;
import github.totorewa.rugserver.logging.LogSubscriptionPersistenceHandler;
import github.totorewa.rugserver.settings.SettingsManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import static com.google.common.base.Preconditions.checkNotNull;

public class RugServerMod implements ModInitializer, Tickable {
    public static RugServerMod mod;
    public MinecraftServer server;
    private LogSubscriptionPersistenceHandler logSubscriptionPersistenceHandler;

    @Override
    public void onInitialize() {
        mod = this;
        MinecraftServer.getServer().addTickable(this);
        InfoLogger.registerLoggers();
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
        logSubscriptionPersistenceHandler = new LogSubscriptionPersistenceHandler(server);
        SettingsManager.initialize(server);
        SettingsManager.register(RugSettings.class);
        server.addTickable(this);
        InfoLogger.registerLoggers();
        if (RugSettings.persistLogSubscriptions)
            logSubscriptionPersistenceHandler.load();
    }

    public LogSubscriptionPersistenceHandler getLogSubscriptionPersistenceHandler() {
        return logSubscriptionPersistenceHandler;
    }
}
