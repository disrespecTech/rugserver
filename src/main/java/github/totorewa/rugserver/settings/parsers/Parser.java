package github.totorewa.rugserver.settings.parsers;

import github.totorewa.rugserver.util.ResultOrError;

public interface Parser<E> {
    ResultOrError<E> parseValue(String value);
    String asString(E value);
    Iterable<String> getDefaultOptions();
}
