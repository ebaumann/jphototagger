package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.view.dialogs.AdvancedSearchDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Dialog für erweiterte Suche anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public final class ControllerShowAdvancedSearchDialog extends Controller
    implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            showDialog();
        }
    }

    private void showDialog() {
        AdvancedSearchDialog dialog = AdvancedSearchDialog.getInstance();
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }
}
