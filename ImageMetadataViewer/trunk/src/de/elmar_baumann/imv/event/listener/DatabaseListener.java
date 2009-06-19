package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;

/**
 * Listens to events in the database.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public interface DatabaseListener {

    /**
     * An image event occured.
     * 
     * @param event event
     */
    public void actionPerformed(DatabaseImageEvent event);

    /**
     * A program event occured.
     *
     * @param event event
     */
    public void actionPerformed(DatabaseProgramEvent event);
}
