package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.DatabaseSavedSearches;
import de.elmar_baumann.imv.model.ListModelSavedSearches;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListSavedSearches;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 * Kontrolliert die Aktion: Lösche eine gespeicherte Suche, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuListSavedSearches}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerDeleteSavedSearch implements ActionListener {

    private final DatabaseSavedSearches db = DatabaseSavedSearches.getInstance();
    private final PopupMenuListSavedSearches actionPopup = PopupMenuListSavedSearches.getInstance();
    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JList list = appPanel.getListSavedSearches();
    private final ListModelSavedSearches model = (ListModelSavedSearches) list.getModel();

    public ControllerDeleteSavedSearch() {
        actionPopup.addActionListenerDelete(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        delete();
    }

    private void delete() {
        SavedSearch search = actionPopup.getSavedSearch();
        String searchName = search.getParamStatements().getName();
        if (deleteConfirmed(searchName)) {
            if (db.deleteSavedSearch(searchName)) {
                model.removeElement(search);
            } else {
                messageErrorDelete();
            }
        }
    }

    private boolean deleteConfirmed(String name) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ControllerDeleteSavedSearch.ConfirmMessage.DeleteSearch"));
        Object[] params = {name};
        return JOptionPane.showConfirmDialog(null, msg.format(params),
                Bundle.getString("ControllerDeleteSavedSearch.ConfirmMessage.DeleteSearch.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private void messageErrorDelete() {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString("ControllerDeleteSavedSearch.ErrorMessage.SavedSearchCouldntBeDeleted"),
                Bundle.getString("ControllerDeleteSavedSearch.ErrorMessage.SavedSearchCouldntBeDeleted.Title"),
                JOptionPane.ERROR_MESSAGE,
                AppSettings.getMediumAppIcon());
    }
}
