package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.view.dialogs.AdvancedSearchDialog;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Bearbeite eine gespeicherte Suche, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerEditSafedSearch implements ActionListener {

    private final PopupMenuSavedSearches actionPopup =
            PopupMenuSavedSearches.INSTANCE;

    public ControllerEditSafedSearch() {
        listen();
    }

    private void listen() {
        actionPopup.getItemEdit().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showAdvancedSearchDialog();
    }

    private void showAdvancedSearchDialog() {
        AdvancedSearchDialog dialog = AdvancedSearchDialog.INSTANCE;
        dialog.setSavedSearch(actionPopup.getSavedSearch());
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }
}
