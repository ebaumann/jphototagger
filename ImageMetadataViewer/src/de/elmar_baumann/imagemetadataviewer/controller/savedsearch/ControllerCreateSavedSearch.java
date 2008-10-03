package de.elmar_baumann.imagemetadataviewer.controller.savedsearch;

import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.data.SavedSearch;
import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.event.SearchEvent;
import de.elmar_baumann.imagemetadataviewer.event.SearchListener;
import de.elmar_baumann.imagemetadataviewer.model.TreeModelSavedSearches;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.dialogs.AdvancedSearchDialog;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuTreeSavedSearches;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JTree;

/**
 * Controller für die Aktion: Erzeuge eine gespeicherte Suche, ausgelöst von
 * {@link de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuTreeSavedSearches}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class ControllerCreateSavedSearch extends Controller
    implements ActionListener, SearchListener {

    private Database db = Database.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JTree tree = appPanel.getTreeSavedSearches();
    private TreeModelSavedSearches model = (TreeModelSavedSearches) tree.getModel();

    public ControllerCreateSavedSearch() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        PopupMenuTreeSavedSearches.getInstance().addActionListenerCreate(this);
        AdvancedSearchDialog.getInstance().addSearchListener(this);
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
                model.addNode(savedSearch);
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
                AppSettings.getSmallAppIcon()) == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private void errorMessageSave() {
        JOptionPane.showMessageDialog(
            null,
            Bundle.getString("ControllerRenameSavedSearch.ErrorMessage.SearchCouldntBeSaved"),
            Bundle.getString("ControllerRenameSavedSearch.ErrorMessage.SearchCouldntBeSaved.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getSmallAppIcon());
    }
}
