package github.totorewa.rugserver.logging;

import github.totorewa.rugserver.helper.MessageHelper;
import github.totorewa.rugserver.fake.IMobSpawnerHelper;
import github.totorewa.rugserver.mixin.logging.MobSpawnerHelperAccessor;
import github.totorewa.rugserver.mixin.logging.ServerWorldAccessor;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.MobSpawnerHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Consumer;

public class InfoLogger {
    private final Set<String> entitiesLogging = new HashSet<>();
    public final String name;
    private final LoggingTick ticker;

    protected InfoLogger(String name, LoggingTick ticker) {
        this.name = name;
        this.ticker = ticker;
    }

    public void startLogging(String username) {
        entitiesLogging.add(username);
    }

    public void stopLogging(String username) {
        entitiesLogging.remove(username);
    }

    public boolean toggleLogging(String username) {
        if (entitiesLogging.contains(username)) {
            stopLogging(username);
            return false;
        }
        startLogging(username);
        return true;
    }

    public boolean isLogging(String username) {
        return entitiesLogging.contains(username);
    }

    public void initialize() {
    }

    public void tick(ServerWorld world, long tick) {
        if (tick % ticker.getInterval() == 0 && ticker.shouldTick(world))
            ticker.tick(world, this, tick);
    }

    public void applyForPlayers(final Consumer<ServerPlayerEntity> consumer) {
        final List<ServerPlayerEntity> players = MinecraftServer.getServer().getPlayerManager().getPlayers();
        for (ServerPlayerEntity player : players) {
            if (entitiesLogging.contains(player.getGameProfile().getName()))
                consumer.accept(player);
        }
    }

    public static Map<String, InfoLogger> loggers = new HashMap<>();

    public static void registerLoggers() {
        final InfoLogger tpsInfo = new FooterLogger("tps", (world, logger, tick) -> {
            double mspt = MathHelper.average(world.getServer().lastTickLengths) * 1.0E-6D;
            double tps = 1000.0D / Math.max(50.0D, mspt);
            int heatStyle = MessageHelper.getHeatmapColor(mspt, 50);
            Message tpsFooter = new Message("TPS: ", Message.GRAY)
                    .add(MessageHelper.formatDouble(tps), heatStyle)
                    .add(" MSPT: ", Message.GRAY)
                    .add(MessageHelper.formatDouble(mspt), heatStyle);
            ((FooterLogger) logger).footer.setFooter(tpsFooter);
        });

        final InfoLogger mobcapInfo = new FooterLogger("mobcaps", new LoggingTick() {
            @Override
            public boolean shouldTick(World world) {
                return true;
            }

            @Override
            public void tick(ServerWorld world, InfoLogger logger, long tick) {
                Message footer = null;
                MobSpawnerHelper mobSpawnerHelper = ((ServerWorldAccessor) world).getMobSpawnerHelper();
                int chunks = ((IMobSpawnerHelper) (Object) mobSpawnerHelper).getLastChunkCount();
                for (EntityCategory category : EntityCategory.values()) {
                    if (footer == null) footer = new Message();
                    else footer.add("  ", Message.WHITE);
                    int count = world.method_3616(category.getCategoryClass());
                    int cap = category.getSpawnCap() * chunks / MobSpawnerHelperAccessor.getMagicNumber();
                    footer.add(String.valueOf(count), MessageHelper.getHeatmapColor(count, cap))
                            .add("/", Message.RESET)
                            .add(String.valueOf(cap), MessageHelper.getEntityCategoryColor(category));
                }
                if (footer != null) ((FooterLogger) logger).footer.setFooter(footer);
            }
        });

        final InfoLogger carefulBreak = new InfoLogger("carefulBreak", new LoggingTick() {
            @Override
            public boolean shouldTick(World world) {
                return false;
            }

            @Override
            public void tick(ServerWorld world, InfoLogger logger, long tick) {

            }
        });

        tpsInfo.initialize();
        mobcapInfo.initialize();
        carefulBreak.initialize();

        loggers.put("tps", tpsInfo);
        loggers.put("mobcaps", mobcapInfo);
        loggers.put("carefulBreak", carefulBreak);
    }

    public static InfoLogger getLogger(String name) {
        return loggers.get(name);
    }
}
