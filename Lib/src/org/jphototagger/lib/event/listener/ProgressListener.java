package org.jphototagger.lib.event.listener;

import org.jphototagger.lib.event.ProgressEvent;

/**
 * Wird über einen Fortschritt benachrichtigt.
 *
 * @author Elmar Baumann
 */
public interface ProgressListener {

    /**
     * Startereignis.
     *
     * @param evt Ereignis
     */
    void progressStarted(ProgressEvent evt);

    /**
     * Ein Ereignis wurde abgearbeitet.
     *
     * @param evt Ereignis
     */
    void progressPerformed(ProgressEvent evt);

    /**
     * Alle Ereignisse sind abgearbeitet.
     *
     * @param evt Ereignis
     */
    void progressEnded(ProgressEvent evt);
}
