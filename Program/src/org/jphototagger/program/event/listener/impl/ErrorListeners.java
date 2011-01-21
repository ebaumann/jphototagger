package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.event.listener.ErrorListener;

/**
 * Error-Listener, erspart Klassen die Implementation von
 * <code>add()</code> und <code>removeErrorListener()</code>.
 * Diese rufen statt dessen bei dieser Instanz auf:
 * {@link #notifyListeners(Object, String)}.
 *
 * Klassen, die sich für Fehler interessieren, melden sich bei der Instanz
 * dieser Klasse an.
 *
 * @author Elmar Baumann
 */
public final class ErrorListeners extends ListenerSupport<ErrorListener> {
    public static final ErrorListeners INSTANCE = new ErrorListeners();

    public void notifyListeners(Object source, String message) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (message == null) {
            throw new NullPointerException("message == null");
        }

        for (ErrorListener listener : listeners) {
            listener.error(source, message);
        }
    }

    private ErrorListeners() {}
}
