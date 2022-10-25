package github.totorewa.rugserver.logging;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogSubscriptionPersistenceHandler {
    public static final String FILENAME = "rug-subs.conf";
    private final File file;

    public LogSubscriptionPersistenceHandler(MinecraftServer server) {
        ServerWorld world = server.getWorld(0);
        if (world == null) {
            file = null;
            return;
        }
        file = new File(world.getSaveHandler().getWorldFolder(), FILENAME);
    }

    public void save() {
        if (file == null) return;
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            Map<String, List<String>> logMap = new HashMap<>();
            for (InfoLogger logger : InfoLogger.loggers.values()) {
                for (String player : logger.getNamesForSubscribedPlayers()) {
                    if (!logMap.containsKey(player))
                        logMap.put(player, Lists.newArrayList(logger.name));
                    else logMap.get(player).add(logger.name);
                }
            }
            for (Map.Entry<String, List<String>> entry : logMap.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    writer.write(entry.getKey());
                    for (String logname : entry.getValue()) {
                        writer.write(" " + logname);
                    }
                    writer.write("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void load() {
        if (file == null || !file.exists()) return;
        BufferedReader reader = null;
        try {
            reader = Files.newReader(file, Charsets.UTF_8);
            Map<String, List<String>> logMap = new HashMap<>(InfoLogger.loggers.size());
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("\\r|\\n", "");
                String[] parts = line.split("\\s+");
                if (parts.length >= 2 && !parts[0].isEmpty()) {
                    for (int i = 1; i < parts.length; i++) {
                        String log = parts[i];
                        if (log.isEmpty()) continue;
                        if (!logMap.containsKey(log)) logMap.put(log, Lists.newArrayList(parts[0]));
                        else logMap.get(log).add(parts[0]);
                    }
                }
            }
            for (InfoLogger logger : InfoLogger.loggers.values()) {
                for (String player : logger.getNamesForSubscribedPlayers()) {
                    logger.stopLogging(player);
                }
                if (logMap.containsKey(logger.name)) {
                    for (String player : logMap.get(logger.name)) {
                        logger.startLogging(player);
                    }
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
