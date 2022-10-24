package github.totorewa.rugserver.util;

public interface Mapper<E1, E2> {
    E2 map(E1 value);
}
