package de.elmar_baumann.imv.controller.savedsearch;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.Database;
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
 * Kontrolliert die Aktion: Benenne eine gespeicherte Suche um, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuListSavedSearches}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class ControllerRenameSavedSearch extends Controller
    implements ActionListener {

    private Database db = Database.getInstance();
    private PopupMenuListSavedSearches actionPopup = PopupMenuListSavedSearches.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JList list = appPanel.getListSavedSearches();
    private ListModelSavedSearches model = (ListModelSavedSearches) list.getModel();

    public ControllerRenameSavedSearch() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        actionPopup.addActionListenerRename(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            rename();
        }
    }

    private void rename() {
        SavedSearch oldSearch = actionPopup.getSavedSearch();
        String oldName = oldSearch.getName();
        String newName = getNewName(oldName);
        if (newName != null &&
            db.updateRenameSavedSearch(oldName, newName)) {
            SavedSearch newSearch = db.getSavedSearch(newName);
            if (newSearch == null) {
                messageErrorRenameGetUpdate(oldName);
            } else {
                model.rename(oldSearch, newSearch);
            }
        } else {
            messageErrorRename(oldName);
        }
    }

    private String getNewName(String oldName) {
        boolean inputRequestet = true;
        String input = null;
        while (inputRequestet) {
            input = JOptionPane.showInputDialog(null, Bundle.getString("ControllerRenameSavedSearch.Input.NewName"), oldName);
            inputRequestet = false;
            if (input != null && db.existsSavedSearch(input)) {
                MessageFormat message = new MessageFormat(
                    Bundle.getString("ControllerRenameSavedSearch.ConfirmMessage.ChangeNameBecauseExists"));
                Object[] params = {input};
                inputRequestet = JOptionPane.showConfirmDialog(null,
                    message.format(params),
                    Bundle.getString("ControllerRenameSavedSearch.ConfirmMessage.ChangeNameBecauseExists.Title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
                if (inputRequestet) {
                    oldName = input;
                }
                input = null;
            }
        }
        return input;
    }

    private void messageErrorRename(String searchName) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ControllerRenameSavedSearch.ErrorMessage.RenameFailed"));
        Object[] param = {searchName};
        JOptionPane.showMessageDialog(
            null,
            msg.format(param),
            Bundle.getString("ControllerRenameSavedSearch.ErrorMessage.RenameFailed.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }

    private void messageErrorRenameGetUpdate(String searchName) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ControllerRenameSavedSearch.ErrorMessage.SavedSearchWasRenamedButCouldntBeLoaded"));
        Object[] param = {searchName};
        JOptionPane.showMessageDialog(
            null,
            msg.format(param),
            Bundle.getString("ControllerRenameSavedSearch.ErrorMessage.SavedSearchWasRenamedButCouldntBeLoadedTitle"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }
}
