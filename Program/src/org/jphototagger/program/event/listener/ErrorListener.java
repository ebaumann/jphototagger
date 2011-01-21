package org.jphototagger.program.event.listener;

/**
 * Listens to errors.
 *
 * @author Elmar Baumann
 */
public interface ErrorListener {

    void error(Object source, String message);
}
