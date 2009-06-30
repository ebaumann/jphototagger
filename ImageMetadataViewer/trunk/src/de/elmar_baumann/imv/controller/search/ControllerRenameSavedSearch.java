package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.tasks.SavedSearchesModifier;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Benenne eine gespeicherte Suche um, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerRenameSavedSearch implements ActionListener {

    private final PopupMenuSavedSearches actionPopup =
            PopupMenuSavedSearches.INSTANCE;

    public ControllerRenameSavedSearch() {
        listen();
    }

    private void listen() {
        actionPopup.getItemRename().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        rename();
    }

    private void rename() {
        SavedSearchesModifier.rename(actionPopup.getSavedSearch());
    }
}
