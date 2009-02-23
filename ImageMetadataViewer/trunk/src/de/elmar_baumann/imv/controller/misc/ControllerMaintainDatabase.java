package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.view.dialogs.DatabaseMaintainanceDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Datenbank warten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/12
 */
public final class ControllerMaintainDatabase implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        maintainDatabase();
    }

    private void maintainDatabase() {
        DatabaseMaintainanceDialog maintainanceDialog = DatabaseMaintainanceDialog.INSTANCE;
        if (maintainanceDialog.isVisible()) {
            maintainanceDialog.toFront();
        } else {
            maintainanceDialog.setVisible(true);
        }
    }
}
