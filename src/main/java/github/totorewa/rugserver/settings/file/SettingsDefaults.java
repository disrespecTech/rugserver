package github.totorewa.rugserver.settings.file;

import github.totorewa.rugserver.settings.RugRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsDefaults {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String FILENAME = "rug.conf";
    private final File defaultsFile;

    public SettingsDefaults(File defaultsFile) {
        this.defaultsFile = defaultsFile;
    }

    public Map<String, String> getDefaults() {
        final Map<String, String> map = new HashMap<>();
        if (!defaultsFile.exists()) return map;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(defaultsFile);
            loadDefaults(fileReader, map);
        } catch (Exception exception) {
            LOGGER.warn("Failed to load " + defaultsFile, exception);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                }
            }
        }
        return map;
    }

    public void saveDefaults(Iterable<RugRule<?>> rules) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(defaultsFile);
            for (RugRule<?> rule : rules) {
                if (rule != null && rule.hasDefaultChanged())
                    writer.write(String.format("%s %s\n", rule.name, rule.readDefault()));
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

    private void loadDefaults(Reader stream, Map<String, String> map) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(stream);
            String line;
            while ((line = reader.readLine()) != null) {
                readDefaultLine(line, map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void readDefaultLine(String line, Map<String, String> map) {
        line = line.replaceAll("\\r|\\n", "");
        String[] parts = line.split("\\s+", 2);
        if (parts.length >= 2 && !parts[0].isEmpty() && !parts[1].isEmpty()) {
            map.put(parts[0], parts[1]);
        }
    }
}
