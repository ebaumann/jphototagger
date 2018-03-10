package org.jphototagger.api.function;

/**
 * @author Elmar Baumann
 *
 * @param <T>
 */
public interface Consumer<T> {

    void accept(T t);
}
