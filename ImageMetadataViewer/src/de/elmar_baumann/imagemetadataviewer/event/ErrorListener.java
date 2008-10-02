package de.elmar_baumann.imagemetadataviewer.event;

/**
 * Beobachtet Fehler (in Threads).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/03
 */
public interface ErrorListener {

    /**
     * Wird bei Fehlern aufgerufen.
     * 
     * @param evt Fehlerereigis
     */
    public void error(ErrorEvent evt);
}
