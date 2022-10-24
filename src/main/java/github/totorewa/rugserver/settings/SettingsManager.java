package github.totorewa.rugserver.settings;

import com.google.common.collect.Maps;
import github.totorewa.rugserver.settings.file.SettingsDefaults;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SettingsManager {
    private static final int TARGET_MODIFIERS = Modifier.PUBLIC | Modifier.STATIC;
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, RugRule<?>> rules = Maps.newHashMap();
    private Map<String, String> defaultRuleValues = null;
    public SettingsDefaults settingsDefaultsHandler = null;

    private List<RugRule<?>> registerSettings(Class<?> settingsClass) {
        return Arrays.stream(settingsClass.getDeclaredFields())
                .filter(f -> (f.getModifiers() & TARGET_MODIFIERS) == TARGET_MODIFIERS && f.isAnnotationPresent(Rule.class))
                .map(this::addFieldAsRule)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private RugRule<?> addFieldAsRule(Field field) {
        try {
            RugRule<?> rule = RugRule.class.getDeclaredConstructor(Class.class, Field.class).newInstance(field.getType(), field);
            rules.put(rule.name, rule);
            return rule;
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void register(Class<?> settingsClass) {
        List<RugRule<?>> addedRules = getInstance().registerSettings(settingsClass);
        for (RugRule<?> rule : addedRules) {
            if (instance.defaultRuleValues.containsKey(rule.name)) {
                String value = instance.defaultRuleValues.get(rule.name);
                String error = rule.writeDefault(value);
                if (error != null) {
                    LOGGER.warn(String.format("Failed to load default value for rug rule %s: %s", rule.name, error));
                    continue;
                }
                rule.write(value);
            }
        }
    }

    public static boolean setRule(CommandSource source, String ruleName, String value) {
        if (!ensureRuleExists(source, ruleName))
            return false;

        RugRule<?> rule = getInstance().rules.get(ruleName);
        String error = rule.write(value);
        if (error != null) {
            source.sendMessage(new Message("Rule ", Message.RED)
                    .add(rule.name, Message.BOLD)
                    .add(" could not be set to ", Message.RESET | Message.RED)
                    .add(value, Message.BOLD)
                    .add(":\n", Message.RESET | Message.RED)
                    .add(error, Message.ITALIC)
                    .toText());
            return false;
        }
        return true;
    }

    public static boolean setDefaultRule(CommandSource source, String ruleName, String defaultValue) {
        if (getInstance().settingsDefaultsHandler == null)
            source.sendMessage(new Message("Unable to save defaults", Message.RED).toText());
        if (!ensureRuleExists(source, ruleName))
            return false;

        RugRule<?> rule = getInstance().rules.get(ruleName);
        String error = rule.writeDefault(defaultValue);
        if (error != null) {
            source.sendMessage(new Message("Rule ", Message.RED)
                    .add(rule.name, Message.BOLD)
                    .add(" could not be defaulted to ", Message.RESET | Message.RED)
                    .add(defaultValue, Message.BOLD)
                    .add(":\n", Message.RESET | Message.RED)
                    .add(error, Message.ITALIC)
                    .toText());
            return false;
        }
        instance.settingsDefaultsHandler.saveDefaults(instance.rules.values());
        return true;
    }

    public static void removeDefault(CommandSource source, String ruleName) {
        if (!ensureRuleExists(source, ruleName)) return;
        getInstance().rules.get(ruleName).resetDefault();
        instance.settingsDefaultsHandler.saveDefaults(instance.rules.values());
    }

    public static Iterable<String> getRuleNames() {
        return getInstance().rules.keySet();
    }

    public static RugRule<?> getRule(String ruleName) {
        return getInstance().rules.get(ruleName);
    }

    public static void initialize(MinecraftServer server) {
        ServerWorld world = server.getWorld(0);
        getInstance().settingsDefaultsHandler = world != null
                ? new SettingsDefaults(new File(world.getSaveHandler().getWorldFolder(), SettingsDefaults.FILENAME))
                : null;
        if (instance.settingsDefaultsHandler != null) {
            instance.defaultRuleValues = instance.settingsDefaultsHandler.getDefaults();
        }
    }

    private static boolean ensureRuleExists(CommandSource source, String ruleName) {
        if (!instance.rules.containsKey(ruleName)) {
            source.sendMessage(new Message("Rule ", Message.RED)
                    .add(ruleName, Message.BOLD)
                    .add(" does not exist", Message.RESET | Message.RED)
                    .toText());
            return false;
        }
        return true;
    }

    private static SettingsManager instance;

    private static SettingsManager getInstance() {
        return instance != null ? instance : (instance = new SettingsManager());
    }
}
