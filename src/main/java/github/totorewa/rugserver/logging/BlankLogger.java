package github.totorewa.rugserver.logging;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.function.BooleanSupplier;

public class BlankLogger extends InfoLogger {
    private static LoggingTick BLANK_TICKER = new BlankTicker();
    private final BooleanSupplier enabledSupplier;

    protected BlankLogger(String name, BooleanSupplier enabledSupplier) {
        super(name, BLANK_TICKER);
        this.enabledSupplier = enabledSupplier;
    }

    @Override
    public boolean isEnabled() {
        return enabledSupplier.getAsBoolean();
    }

    private static class BlankTicker implements LoggingTick {
        @Override
        public boolean shouldTick(World world) {
            return false;
        }

        @Override
        public void tick(ServerWorld world, InfoLogger logger, long tick) {
        }
    }
}
