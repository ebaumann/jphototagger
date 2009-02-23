package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.view.dialogs.UpdateMetadataOfDirectoriesDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Dialog zum Aktualisieren der Metadaten anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public final class ControllerShowUpdateMetadataDialog implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        showDialog();
    }

    private void showDialog() {
        UpdateMetadataOfDirectoriesDialog dialog = UpdateMetadataOfDirectoriesDialog.INSTANCE;
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }
}
