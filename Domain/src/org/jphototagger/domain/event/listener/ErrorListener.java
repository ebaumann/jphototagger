package org.jphototagger.domain.event.listener;

/**
 * Listens to errors.
 *
 * @author Elmar Baumann
 */
public interface ErrorListener {

    void error(Object source, String message);
}
