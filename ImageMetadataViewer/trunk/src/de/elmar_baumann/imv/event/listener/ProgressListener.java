package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.event.ProgressEvent;

/**
 * Wird über einen Fortschritt benachrichtigt.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/22
 */
public interface ProgressListener {

    /**
     * Startereignis.
     * 
     * @param evt Ereignis
     */
    public void progressStarted(ProgressEvent evt);

    /**
     * Ein Ereignis wurde abgearbeitet.
     * 
     * @param evt Ereignis
     */
    public void progressPerformed(ProgressEvent evt);

    /**
     * Alle Ereignisse sind abgearbeitet.
     * 
     * @param evt Ereignis
     */
    public void progressEnded(ProgressEvent evt);
}
