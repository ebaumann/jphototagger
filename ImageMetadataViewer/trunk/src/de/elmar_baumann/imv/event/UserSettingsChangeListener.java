package de.elmar_baumann.imv.event;

/**
 * Beobachtet Änderungen der Benutzereinstellungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public interface UserSettingsChangeListener {

    /**
     * Wende die neuen Einstellungen des Benutzers an.
     * 
     * @param evt Ereignis
     */
    void applySettings(UserSettingsChangeEvent evt);
}
