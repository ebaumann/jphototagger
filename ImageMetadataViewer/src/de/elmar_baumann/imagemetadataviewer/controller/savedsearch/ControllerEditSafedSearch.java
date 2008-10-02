package de.elmar_baumann.imagemetadataviewer.controller.savedsearch;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.view.dialogs.AdvancedSearchDialog;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuTreeSavedSearches;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Bearbeite eine gespeicherte Suche, ausgelöst von
 * {@link de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuTreeSavedSearches}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class ControllerEditSafedSearch extends Controller
    implements ActionListener {

    private PopupMenuTreeSavedSearches actionPopup = PopupMenuTreeSavedSearches.getInstance();

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
