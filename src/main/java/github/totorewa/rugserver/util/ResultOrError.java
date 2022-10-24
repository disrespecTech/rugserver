package github.totorewa.rugserver.util;

public abstract class ResultOrError<E> {
    public static <E> ResultOrError<E> result(E value) {
        return new WithResult<>(value);
    }

    public static <E> ResultOrError<E> error(String error) {
        return new WithError<>(error);
    }

    public abstract <E2> ResultOrError<E2> map(Mapper<E, E2> mapper);

    public boolean hasError() {
        return false;
    }

    public E get() {
        return null;
    }

    public String getError() {
        return "";
    }
}

class WithResult<E> extends ResultOrError<E> {
    private final E value;

    WithResult(E value) {
        this.value = value;
    }

    @Override
    public <E2> ResultOrError<E2> map(Mapper<E, E2> mapper) {
        return ResultOrError.result(mapper.map(value));
    }

    @Override
    public E get() {
        return value;
    }
}

class WithError<E> extends ResultOrError<E> {
    private final String error;

    WithError(String error) {
        this.error = error;
    }

    @Override
    public <E2> ResultOrError<E2> map(Mapper<E, E2> mapper) {
        return ResultOrError.error(error);
    }

    @Override
    public boolean hasError() {
        return true;
    }

    @Override
    public String getError() {
        return error;
    }
}
