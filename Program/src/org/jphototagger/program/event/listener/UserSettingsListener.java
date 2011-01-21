package org.jphototagger.program.event.listener;

import org.jphototagger.program.event.UserSettingsEvent;

/**
 * Beobachtet Änderungen der Benutzereinstellungen.
 *
 * @author Elmar Baumann
 */
public interface UserSettingsListener {

    /**
     * Wende die neuen Einstellungen des Benutzers an.
     *
     * @param evt Ereignis
     */
    void applySettings(UserSettingsEvent evt);
}
