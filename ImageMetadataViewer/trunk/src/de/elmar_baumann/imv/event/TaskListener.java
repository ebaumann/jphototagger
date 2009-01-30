package de.elmar_baumann.imv.event;

/**
 * Beobachtet Tasks.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public interface TaskListener {

    /**
     * Wird aufgerufen beim Beobachter, wenn der Task beendet ist.
     */
    public void taskCompleted();
}
