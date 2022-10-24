package github.totorewa.rugserver.settings.parsers;

import java.util.HashMap;
import java.util.Map;

public class ParserProvider {
    private static Parser<Boolean> booleanParser;
    private static Parser<Integer> intParser;
    private static Map<Class<?>, Parser<?>> enumParsers;

    public static <E> Parser<E> get(Class<E> clazz) {
        if (boolean.class.equals(clazz))
            return (Parser<E>) booleanParser;
        if (int.class.equals(clazz))
            return (Parser<E>) intParser;
        if (clazz.isEnum()) {
            if (!enumParsers.containsKey(clazz))
                enumParsers.put(clazz, new EnumParser<>(clazz));
            Parser<E> enumParser = (Parser<E>) enumParsers.get(clazz);
            if (enumParser != null)
                return enumParser;
        }
        throw new IllegalArgumentException("No parser for type " + clazz.getName());
    }

    static {
        booleanParser = new BooleanParser();
        intParser = new IntegerParser();
        enumParsers = new HashMap<>();
    }
}
