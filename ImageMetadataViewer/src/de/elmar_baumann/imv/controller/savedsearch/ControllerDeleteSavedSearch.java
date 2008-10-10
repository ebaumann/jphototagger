package de.elmar_baumann.imv.controller.savedsearch;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.model.TreeModelSavedSearches;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeSavedSearches;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JOptionPane;
import javax.swing.JTree;

/**
 * Kontrolliert die Aktion: Lösche eine gespeicherte Suche, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeSavedSearches}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class ControllerDeleteSavedSearch extends Controller
    implements ActionListener {

    private Database db = Database.getInstance();
    private PopupMenuTreeSavedSearches actionPopup = PopupMenuTreeSavedSearches.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JTree tree = appPanel.getTreeSavedSearches();
    private TreeModelSavedSearches model = (TreeModelSavedSearches) tree.getModel();

    public ControllerDeleteSavedSearch() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        actionPopup.addActionListenerDelete(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            delete();
        }
    }

    private void delete() {
        SavedSearch search = actionPopup.getSavedSearch();
        String searchName = search.getParamStatements().getName();
        if (askDeleteSearch(searchName) &&
            db.deleteSavedSearch(searchName)) {
            model.removeNode(search);
        } else {
            messageErrorDelete();
        }
    }

    private boolean askDeleteSearch(String name) {
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
