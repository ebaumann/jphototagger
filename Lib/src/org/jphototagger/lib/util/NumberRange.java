package org.jphototagger.lib.util;

/**
 * @param <T>
 * @author Elmar Baumann
 */
public final class NumberRange<T extends Number> {

    private final T begin;
    private final T end;

    public NumberRange(T begin, T end) {
        if (begin == null) {
            throw new NullPointerException("begin == null");
        }

        if (end == null) {
            throw new NullPointerException("end == null");
        }

        this.begin = begin;
        this.end = end;
    }

    public T getBegin() {
        return begin;
    }

    public T getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof NumberRange)) {
            return false;
        }

        NumberRange<?> other = (NumberRange) obj;

        return begin.equals(other.begin) && end.equals(other.end);
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 23 * hash + begin.hashCode();
        hash = 23 * hash + end.hashCode();

        return hash;
    }
}
