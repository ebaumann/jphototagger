package de.elmar_baumann.imv.event.listener;

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
public class ErrorListeners {

    private static ErrorListeners instance = new ErrorListeners();
    private List<ErrorListener> errorListeners;

    /**
     * Meldet einen Beobachter an.
     * 
     * @param listener  Beobachter
     */
    public void addErrorListener(ErrorListener listener) {
        errorListeners.add(listener);
    }

    /**
     * Meldet einen Beobachter ab.
     * 
     * @param listener  Beobachter
     */
    public void removeErrorListener(ErrorListener listener) {
        errorListeners.remove(listener);
    }

    /**
     * Meldet allen angemeldeten Errorlistenern einen Fehler.
     * 
     * @param evt  Fehlerereignis
     */
    public void notifyErrorListener(ErrorEvent evt) {
        for (ErrorListener listener : errorListeners) {
            listener.error(evt);
        }
    }

    /**
     * Liefert die einzige Instanz dieser Klasse.
     * 
     * @return Instanz
     */
    public static ErrorListeners getInstance() {
        return instance;
    }

    private ErrorListeners() {
        errorListeners = new ArrayList<ErrorListener>();
    }
}
