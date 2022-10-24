package github.totorewa.rugserver.settings;

import com.google.common.collect.Lists;
import github.totorewa.rugserver.settings.parsers.Parser;
import github.totorewa.rugserver.settings.parsers.ParserProvider;
import github.totorewa.rugserver.util.ResultOrError;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class RugRule<E> {
    private final Field field;
    private final E initialValue;
    private E defaultValue;
    private Parser<E> parser;
    private final List<Validator<E>> validators = Lists.newArrayList();
    public final Rule ruleMeta;
    public final String name;
    public final List<String> options = Lists.newArrayList();

    public RugRule(Class<E> clazz, Field field) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        name = field.getName();
        this.field = field;
        initialValue = (E) field.get(null);
        defaultValue = initialValue;

        ruleMeta = field.getAnnotation(Rule.class);
        for (Class<? extends Validator> validatorClass : ruleMeta.validator()) {
            validators.add(validatorClass.getConstructor().newInstance());
        }
        parser = ParserProvider.get(clazz);
        String[] options = ruleMeta.options();
        if (options != null && options.length > 0)
            this.options.addAll(Arrays.asList(options));
        else {
            Iterable<String> defaultOptions = parser.getDefaultOptions();
            if (defaultOptions != null)
                defaultOptions.forEach(this.options::add);
        }
    }

    public E current() {
        try {
            return (E) field.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public E getDefault() {
        return defaultValue;
    }

    public E getInitial() {
        return initialValue;
    }

    public boolean hasDefaultChanged() {
        return !initialValue.equals(defaultValue);
    }

    public String write(String value) {
        if (!isValid(value)) return String.format("Unable to set %s to value %s", name, value);
        ResultOrError<E> parsed = parser.parseValue(value);
        if (parsed.hasError()) return parsed.getError();
        try {
            field.set(null, parsed.get());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return String.format("Unable to set %s to value %s", name, value);
        }
        return null;
    }


    public String read() {
        return parser.asString(current());
    }

    public String writeDefault(String value) {
        if (!isValid(value)) return String.format("Unable to set %s default value to %s", name, value);
        ResultOrError<E> parsed = parser.parseValue(value);
        if (parsed.hasError()) return parsed.getError();
        defaultValue = (E) parsed.get();
        return null;
    }


    public String readDefault() {
        return parser.asString(defaultValue);
    }

    public void resetDefault() {
        defaultValue = initialValue;
    }

    private boolean isValid(String value) {
        if (!ruleMeta.strict() || options.isEmpty()) return true;
        for (String opt : options) {
            if (opt.equals(value))
                return true;
        }
        return false;
    }
}
