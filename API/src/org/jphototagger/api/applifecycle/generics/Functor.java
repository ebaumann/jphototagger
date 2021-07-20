package org.jphototagger.api.applifecycle.generics;

/**
 * @param <T> 
 * @author Elmar Baumann
 */
public interface Functor<T> {

    void execute(T t);
}
