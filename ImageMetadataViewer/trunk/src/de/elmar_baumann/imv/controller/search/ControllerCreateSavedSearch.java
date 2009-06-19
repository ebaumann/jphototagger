package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.event.listener.impl.ListenerProvider;
import de.elmar_baumann.imv.event.SearchEvent;
import de.elmar_baumann.imv.event.listener.SearchListener;
import de.elmar_baumann.imv.tasks.SavedSearchesModifier;
import de.elmar_baumann.imv.view.dialogs.AdvancedSearchDialog;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListSavedSearches;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller für die Aktion: Erzeuge eine gespeicherte Suche, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuListSavedSearches}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerCreateSavedSearch implements ActionListener, SearchListener {

    public ControllerCreateSavedSearch() {
        listen();
    }

    private void listen() {
        PopupMenuListSavedSearches.INSTANCE.addActionListenerCreate(this);
        ListenerProvider.INSTANCE.addSearchListener(this);
    }

    @Override
    public void actionPerformed(SearchEvent evt) {
        if (evt.getType().equals(SearchEvent.Type.SAVE)) {
            SavedSearchesModifier.insert(evt.getSafedSearch(), evt.isForceOverwrite());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showAdvancedSearchDialog();
    }

    private void showAdvancedSearchDialog() {
        AdvancedSearchDialog dialog = AdvancedSearchDialog.INSTANCE;
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }

}
