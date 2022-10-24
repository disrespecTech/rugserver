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
    private Parser<E> parser;
    private final List<Validator<E>> validators = Lists.newArrayList();
    public final Rule ruleMeta;
    public final String name;
    public final E defaultValue;
    public final List<String> options = Lists.newArrayList();

    public RugRule(Class<E> clazz, Field field) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        name = field.getName();
        this.field = field;
        defaultValue = (E) field.get(null);

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

    public String write(String value) {
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
}
