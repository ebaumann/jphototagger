package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.tasks.SavedSearchesModifier;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListSavedSearches;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Lösche eine gespeicherte Suche, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuListSavedSearches}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerDeleteSavedSearch implements ActionListener {

    private final PopupMenuListSavedSearches actionPopup = PopupMenuListSavedSearches.INSTANCE;

    public ControllerDeleteSavedSearch() {
        listen();
    }

    private void listen() {
        actionPopup.addActionListenerDelete(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SavedSearchesModifier.delete(actionPopup.getSavedSearch());
    }
}
