package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.event.ErrorEvent;
import de.elmar_baumann.imv.event.ErrorListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Error-Listener, erspart Klassen die Implementation von
 * <code>addErrorListener()</code> und <code>removeErrorListener()</code>.
 * Diese rufen statt dessen bei dieser Instanz auf:
 * {@link #notifyErrorListener(de.elmar_baumann.imv.event.ErrorEvent)}.
 * 
 * Klassen, die sich für Fehler interessieren, melden sich bei der Instanz
 * dieser Klasse an.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public final class ErrorListeners {

    private final List<ErrorListener> errorListeners = new ArrayList<ErrorListener>();
    public static final ErrorListeners INSTANCE = new ErrorListeners();

    /**
     * Meldet einen Beobachter an.
     * 
     * @param listener  Beobachter
     */
    public synchronized void addErrorListener(ErrorListener listener) {
        errorListeners.add(listener);
    }

    /**
     * Meldet allen angemeldeten Errorlistenern einen Fehler.
     * 
     * @param evt  Fehlerereignis
     */
    public synchronized void notifyErrorListener(ErrorEvent evt) {
        for (ErrorListener listener : errorListeners) {
            listener.error(evt);
        }
    }

    private ErrorListeners() {
    }
}
