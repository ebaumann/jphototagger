package de.elmar_baumann.imagemetadataviewer.controller.misc;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.view.dialogs.UserSettingsDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Benutzereinstellungen-Dialog anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public class ControllerShowUserSettingsDialog extends Controller
    implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            showDialog();
        }
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
