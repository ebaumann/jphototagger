package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.comparator.ComparatorSavedSearch;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.DatabaseSavedSearches;
import de.elmar_baumann.imv.model.ListModelSavedSearches;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.componentutil.ListUtil;
import javax.swing.JOptionPane;

/**
 * Inserts, updates, deletes  saved searches into/from the model and database.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/02/25
 */
public final class SavedSearchesModifier {

    /**
     * Inserts a saved search after the user confirms.
     *
     * @param savedSearch  saved search
     * @param force        force insertion even if a saved search with the
     *                     same name exists
     */
    public static void insert(SavedSearch savedSearch, boolean force) {
        if (force || confirmInsert(savedSearch)) {
            if (getDb().insertSavedSearch(savedSearch)) {
                ListModelSavedSearches model = getModel();
                if (!model.contains(savedSearch)) {
                    ListUtil.insertSorted(model, savedSearch, ComparatorSavedSearch.INSTANCE);
                }
            } else {
                errorMessageInsert();
            }
        }
    }

    /**
     * Deletes a saved search after the user confirms.
     *
     * @param search  saved search
     */
    public static void delete(SavedSearch search) {
        String searchName = search.getParamStatements().getName();
        if (confirmDelete(searchName)) {
            if (getDb().deleteSavedSearch(searchName)) {
                ListModelSavedSearches model = getModel();
                model.removeElement(search);
            } else {
                errorMessageDelete();
            }
        }
    }

    /**
     * Renames saved search after the user confirms.
     *
     * @param oldSearch  saved search
     */
    public static void rename(SavedSearch oldSearch) {
        String oldName = oldSearch.getName();
        String newName = getNewName(oldName);
        if (newName != null) {
            DatabaseSavedSearches db = getDb();
            if (db.updateRenameSavedSearch(oldName, newName)) {
                SavedSearch newSearch = db.getSavedSearch(newName);
                if (newSearch == null) {
                    errorMessageRenameGetUpdate(oldName);
                } else {
                    getModel().rename(oldSearch, newSearch);
                }
            } else {
                errorMessageRename(oldName);
            }
        }
    }

    private static String getNewName(String oldName) {
        boolean hasInput = true;
        String input = null;
        while (hasInput) {
            input = getInput(oldName);
            hasInput = false;
            if (input != null && getDb().existsSavedSearch(input)) {
                hasInput = confirmInputDifferentName(input);
                if (hasInput) {
                    oldName = input;
                }
                input = null;
            }
        }
        return input;
    }

    private static DatabaseSavedSearches getDb() {
        DatabaseSavedSearches db = DatabaseSavedSearches.INSTANCE;
        return db;
    }

    private static ListModelSavedSearches getModel() {
        return (ListModelSavedSearches) GUI.INSTANCE.getAppPanel().getListSavedSearches().getModel();
    }

    private static String getInput(String oldName) {
        return JOptionPane.showInputDialog(
                null,
                Bundle.getString("SavedSearchesModifier.Input.NewName"),
                oldName);
    }

    private static boolean confirmInputDifferentName(String input) {
        return JOptionPane.showConfirmDialog(
                null,
                Bundle.getString("SavedSearchesModifier.ConfirmMessage.ChangeNameBecauseExists", input),
                Bundle.getString("SavedSearchesModifier.ConfirmMessage.ChangeNameBecauseExists.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private static void errorMessageRename(String searchName) {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString("SavedSearchesModifier.ErrorMessage.RenameFailed", searchName),
                Bundle.getString("SavedSearchesModifier.ErrorMessage.RenameFailed.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private static void errorMessageRenameGetUpdate(String searchName) {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString("SavedSearchesModifier.ErrorMessage.SavedSearchWasRenamedButCouldntBeLoaded", searchName),
                Bundle.getString("SavedSearchesModifier.ErrorMessage.SavedSearchWasRenamedButCouldntBeLoaded.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private static boolean confirmDelete(String name) {
        return JOptionPane.showConfirmDialog(null,
                Bundle.getString("SavedSearchesModifier.ConfirmMessage.DeleteSearch", name),
                Bundle.getString("SavedSearchesModifier.ConfirmMessage.DeleteSearch.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private static void errorMessageDelete() {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString("SavedSearchesModifier.ErrorMessage.SavedSearchCouldntBeDeleted"),
                Bundle.getString("SavedSearchesModifier.ErrorMessage.SavedSearchCouldntBeDeleted.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private static boolean confirmInsert(SavedSearch savedSearch) {
        if (getDb().existsSavedSearch(savedSearch)) {
            return JOptionPane.showConfirmDialog(null,
                    Bundle.getString("SavedSearchesModifier.ConfirmMessage.ReplaceExisting"),
                    Bundle.getString("SavedSearchesModifier.ConfirmMessage.ReplaceExisting.Title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private static void errorMessageInsert() {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString("SavedSearchesModifier.ErrorMessage.SearchCouldntBeSaved"),
                Bundle.getString("SavedSearchesModifier.ErrorMessage.SearchCouldntBeSaved.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private SavedSearchesModifier() {
    }
}
