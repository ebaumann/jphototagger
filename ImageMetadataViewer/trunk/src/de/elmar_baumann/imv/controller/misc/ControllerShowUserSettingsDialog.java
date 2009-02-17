package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Benutzereinstellungen-Dialog anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public final class ControllerShowUserSettingsDialog implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        showDialog();
    }

    private void showDialog() {
        UserSettingsDialog settingsDialog = UserSettingsDialog.getInstance();
        if (settingsDialog.isVisible()) {
            settingsDialog.toFront();
        } else {
            settingsDialog.setVisible(true);
        }
    }
}
