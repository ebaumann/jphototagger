package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.view.dialogs.UpdateMetadataOfDirectoriesDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Dialog zum Aktualisieren der Metadaten anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public class ControllerShowUpdateMetadataDialog extends Controller
    implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            showDialog();
        }
    }

    private void showDialog() {
        UpdateMetadataOfDirectoriesDialog dialog = UpdateMetadataOfDirectoriesDialog.getInstance();
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }
}
