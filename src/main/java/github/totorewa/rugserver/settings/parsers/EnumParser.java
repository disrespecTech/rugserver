package github.totorewa.rugserver.settings.parsers;

import github.totorewa.rugserver.settings.EnumValue;
import github.totorewa.rugserver.util.ResultOrError;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class EnumParser<E> implements Parser<E> {
    private Field valueField;
    private E[] enumValues;

    public EnumParser(Class<? super E> clazz) {
        if (!clazz.isEnum())
            throw new IllegalArgumentException();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if ((field.getModifiers() & (Modifier.PUBLIC)) > 0 && field.isAnnotationPresent(EnumValue.class)) {
                valueField = field;
                break;
            }
        }
        if (valueField == null)
            throw new RuntimeException(String.format("Enum '%s' does not have a field designated as its config value using @EnumValue", clazz.getName()));
        enumValues = (E[]) clazz.getEnumConstants();
    }

    @Override
    public ResultOrError<E> parseValue(String value) {
        for (E e : enumValues) {
            try {
                String enumValue = String.valueOf(valueField.get(e));
                if (enumValue.equalsIgnoreCase(value))
                    return ResultOrError.result(e);
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
        return ResultOrError.error(String.format("Could not parse '%s' as an enum value", value));
    }

    @Override
    public String asString(E value) {
        try {
            return String.valueOf(valueField.get(value));
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    @Override
    public Iterable<String> getDefaultOptions() {
        List<String> values = new ArrayList<>(enumValues.length);
        for (E e : enumValues) {
            try {
                String enumValue = String.valueOf(valueField.get(e));
                values.add(enumValue);
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
        return values;
    }
}
