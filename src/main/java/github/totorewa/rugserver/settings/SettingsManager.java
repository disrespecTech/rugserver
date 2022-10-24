package github.totorewa.rugserver.settings;

import com.google.common.collect.Maps;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.command.CommandSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

public class SettingsManager {
    private static final int TARGET_MODIFIERS = Modifier.PUBLIC | Modifier.STATIC;
    private final Map<String, RugRule<?>> rules = Maps.newHashMap();

    private void registerSettings(Class<?> settingsClass) {
        Arrays.stream(settingsClass.getDeclaredFields())
                .filter(f -> (f.getModifiers() & TARGET_MODIFIERS) == TARGET_MODIFIERS && f.isAnnotationPresent(Rule.class))
                .forEach(this::addFieldAsRule);
    }

    private void addFieldAsRule(Field field) {
        try {
            RugRule<?> rule = RugRule.class.getDeclaredConstructor(Class.class, Field.class).newInstance(field.getType(), field);
            rules.put(rule.name, rule);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private boolean setValueForRule(CommandSource source, String ruleName, String value) {
        if (!rules.containsKey(ruleName)) {
            source.sendMessage(new Message("Rule ", Message.RED)
                    .add(value, Message.BOLD)
                    .add(" does not exist", Message.RESET | Message.RED)
                    .toText());
            return false;
        }

        RugRule<?> rule = rules.get(ruleName);
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

    public static void register(Class<?> settingsClass) {
        getInstance().registerSettings(settingsClass);
    }

    public static boolean setRule(CommandSource source, String ruleName, String value) {
        return getInstance().setValueForRule(source, ruleName, value);
    }

    public static Iterable<String> getRuleNames() {
        return getInstance().rules.keySet();
    }

    public static RugRule<?> getRule(String ruleName) {
        return getInstance().rules.get(ruleName);
    }

    private static SettingsManager instance;

    private static SettingsManager getInstance() {
        return instance != null ? instance : (instance = new SettingsManager());
    }
}
