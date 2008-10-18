package de.elmar_baumann.imv.controller.savedsearch;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.SearchEvent;
import de.elmar_baumann.imv.event.SearchListener;
import de.elmar_baumann.imv.model.ListModelSavedSearches;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.AdvancedSearchDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListSavedSearches;
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
public class ControllerCreateSavedSearch extends Controller
    implements ActionListener, SearchListener {

    private Database db = Database.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JList list = appPanel.getListSavedSearches();
    private ListModelSavedSearches model = (ListModelSavedSearches) list.getModel();

    public ControllerCreateSavedSearch() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        PopupMenuListSavedSearches.getInstance().addActionListenerCreate(this);
        ListenerProvider.getInstance().addSearchListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            create();
        }
    }

    private void create() {
        AdvancedSearchDialog dialog = AdvancedSearchDialog.getInstance();
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(SearchEvent evt) {
        if (isStarted() && evt.getType().equals(SearchEvent.Type.Save)) {
            saveSearch(evt.getSafedSearch(), evt.isForceOverwrite());
        }
    }

    private void saveSearch(SavedSearch savedSearch, boolean force) {
        if (force || isSave(savedSearch)) {
            if (db.insertSavedSearch(savedSearch)) {
                model.addElement(savedSearch);
            } else {
                errorMessageSave();
            }
        }
    }

    private boolean isSave(SavedSearch savedSearch) {
        if (db.existsSavedSearch(savedSearch)) {
            return JOptionPane.showConfirmDialog(null,
                Bundle.getString("ControllerRenameSavedSearch.ConfirmMessage.ReplaceExisting"),
                Bundle.getString("ControllerRenameSavedSearch.ConfirmMessage.ReplaceExisting.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private void errorMessageSave() {
        JOptionPane.showMessageDialog(
            null,
            Bundle.getString("ControllerRenameSavedSearch.ErrorMessage.SearchCouldntBeSaved"),
            Bundle.getString("ControllerRenameSavedSearch.ErrorMessage.SearchCouldntBeSaved.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }
}
