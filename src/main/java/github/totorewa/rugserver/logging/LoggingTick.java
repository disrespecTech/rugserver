package github.totorewa.rugserver.logging;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public interface LoggingTick {
    default int getInterval() {
        return 20;
    }

    default boolean shouldTick(World world) {
        return world.dimension.getType() == 0;
    }

    void tick(ServerWorld world, InfoLogger logger, long tick);
}
