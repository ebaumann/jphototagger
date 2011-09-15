package org.jphototagger.api.concurrent;

/**
 * Class performing a longer operation which can be cancelled.
 *
 * @author Elmar Baumann
 */
public interface Cancelable {

    /**
     * Does cancel the operation.
     */
    void cancel();
}
