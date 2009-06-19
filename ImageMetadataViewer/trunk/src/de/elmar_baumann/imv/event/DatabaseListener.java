package de.elmar_baumann.imv.event;

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
     * An program event occured.
     *
     * @param event event
     */
    public void actionPerformed(DatabaseProgramEvent event);
}
