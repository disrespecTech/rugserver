package github.totorewa.rugserver.settings.parsers;

import github.totorewa.rugserver.util.ResultOrError;

public class IntegerParser implements Parser<Integer> {
    @Override
    public ResultOrError<Integer> parseValue(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return ResultOrError.result(parsed);
        } catch (NumberFormatException ex) {
            return ResultOrError.error(String.format("Could not parse '%s' as an integer value", value));
        }
    }

    @Override
    public String asString(Integer value) {
        return value.toString();
    }

    @Override
    public Iterable<String> getDefaultOptions() {
        return null;
    }
}
