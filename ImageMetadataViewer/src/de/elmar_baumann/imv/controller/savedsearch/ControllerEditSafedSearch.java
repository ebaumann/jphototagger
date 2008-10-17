package de.elmar_baumann.imv.controller.savedsearch;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.view.dialogs.AdvancedSearchDialog;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListSavedSearches;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Bearbeite eine gespeicherte Suche, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuListSavedSearches}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class ControllerEditSafedSearch extends Controller
    implements ActionListener {

    private PopupMenuListSavedSearches actionPopup = PopupMenuListSavedSearches.getInstance();

    public ControllerEditSafedSearch() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        actionPopup.addActionListenerEdit(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            edit();
        }
    }

    private void edit() {
        AdvancedSearchDialog dialog = AdvancedSearchDialog.getInstance();
        dialog.setSavedSearch(actionPopup.getSavedSearch());
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }
}
