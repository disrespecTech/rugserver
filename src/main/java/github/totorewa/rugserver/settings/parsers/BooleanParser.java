package github.totorewa.rugserver.settings.parsers;

import github.totorewa.rugserver.util.ResultOrError;

import java.util.Arrays;

public class BooleanParser implements Parser<Boolean> {
    private static String[] defaultOptions = {"false", "true"};
    @Override
    public ResultOrError<Boolean> parseValue(String value) {
        value = value.toLowerCase();
        if ("true".equals(value)) return ResultOrError.result(true);
        if ("false".equals(value)) return ResultOrError.result(false);
        return ResultOrError.error(String.format("Could not parse '%s' as a boolean value", value));
    }

    @Override
    public String asString(Boolean value) {
        return value.toString();
    }

    @Override
    public Iterable<String> getDefaultOptions() {
        return Arrays.asList(defaultOptions);
    }
}
