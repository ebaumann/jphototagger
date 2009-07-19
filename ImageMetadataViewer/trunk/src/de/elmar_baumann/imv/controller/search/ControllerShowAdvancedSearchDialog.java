package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.view.dialogs.AdvancedSearchDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Dialog für erweiterte Suche anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-25
 */
public final class ControllerShowAdvancedSearchDialog implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        showDialog();
    }

    private void showDialog() {
        AdvancedSearchDialog dialog = AdvancedSearchDialog.INSTANCE;
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }
}
