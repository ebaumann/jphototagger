package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.comparator.ComparatorSavedSearch;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.DatabaseSavedSearches;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.SearchEvent;
import de.elmar_baumann.imv.event.SearchListener;
import de.elmar_baumann.imv.model.ListModelSavedSearches;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.AdvancedSearchDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListSavedSearches;
import de.elmar_baumann.lib.componentutil.ListUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 * Controller für die Aktion: Erzeuge eine gespeicherte Suche, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuListSavedSearches}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerCreateSavedSearch implements ActionListener, SearchListener {

    private final DatabaseSavedSearches db = DatabaseSavedSearches.getInstance();
    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JList list = appPanel.getListSavedSearches();
    private final ListModelSavedSearches model = (ListModelSavedSearches) list.getModel();

    public ControllerCreateSavedSearch() {
        listen();
    }

    private void listen() {
        PopupMenuListSavedSearches.getInstance().addActionListenerCreate(this);
        ListenerProvider.getInstance().addSearchListener(this);
    }

    @Override
    public void actionPerformed(SearchEvent evt) {
        if (evt.getType().equals(SearchEvent.Type.SAVE)) {
            saveSearch(evt.getSafedSearch(), evt.isForceOverwrite());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showAdvancedSearchDialog();
    }

    private void showAdvancedSearchDialog() {
        AdvancedSearchDialog dialog = AdvancedSearchDialog.getInstance();
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }

    private void saveSearch(SavedSearch savedSearch, boolean force) {
        if (force || saveConfirmed(savedSearch)) {
            if (db.insertSavedSearch(savedSearch)) {
                if (!model.contains(savedSearch)) {
                    ListUtil.insertSorted(model, savedSearch, ComparatorSavedSearch.INSTANCE);
                }
            } else {
                errorMessageSave();
            }
        }
    }

    private boolean saveConfirmed(SavedSearch savedSearch) {
        if (db.existsSavedSearch(savedSearch)) {
            return JOptionPane.showConfirmDialog(null,
                    Bundle.getString("ControllerRenameSavedSearch.ConfirmMessage.ReplaceExisting"),
                    Bundle.getString("ControllerRenameSavedSearch.ConfirmMessage.ReplaceExisting.Title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    AppIcons.getMediumAppIcon()) == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private void errorMessageSave() {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString("ControllerRenameSavedSearch.ErrorMessage.SearchCouldntBeSaved"),
                Bundle.getString("ControllerRenameSavedSearch.ErrorMessage.SearchCouldntBeSaved.Title"),
                JOptionPane.ERROR_MESSAGE,
                AppIcons.getMediumAppIcon());
    }
}
