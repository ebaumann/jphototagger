package de.elmar_baumann.imagemetadataviewer.event;

/**
 * Beobachtet Änderungen der Datenbank.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public interface DatabaseListener {

    /**
     * Die Datenbank wurde modifiziert.
     * 
     * @param action Aktion
     */
    public void actionPerformed(DatabaseAction action);
}
