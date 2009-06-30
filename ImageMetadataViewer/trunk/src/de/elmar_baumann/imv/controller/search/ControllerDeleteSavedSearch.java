package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.tasks.SavedSearchesModifier;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Lösche eine gespeicherte Suche, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerDeleteSavedSearch implements ActionListener {

    private final PopupMenuSavedSearches actionPopup =
            PopupMenuSavedSearches.INSTANCE;

    public ControllerDeleteSavedSearch() {
        listen();
    }

    private void listen() {
        actionPopup.getItemDelete().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SavedSearchesModifier.delete(actionPopup.getSavedSearch());
    }
}
