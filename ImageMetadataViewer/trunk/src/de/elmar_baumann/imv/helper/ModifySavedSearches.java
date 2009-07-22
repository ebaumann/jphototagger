package de.elmar_baumann.imv.helper;

import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.comparator.ComparatorSavedSearch;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.DatabaseSavedSearches;
import de.elmar_baumann.imv.model.ListModelSavedSearches;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.componentutil.ListUtil;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Inserts, updates, deletes  saved searches into/from the model and database.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-02-25
 */
public final class ModifySavedSearches {

    /**
     * Inserts a saved search after the user confirms.
     *
     * @param savedSearch  saved search
     * @param force        force insertion even if a saved search with the
     *                     same name exists
     */
    public static void insert(final SavedSearch savedSearch, boolean force) {
        if (force || confirmInsert(savedSearch)) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (getDb().insertSavedSearch(savedSearch)) {
                        ListModelSavedSearches model = getModel();
                        if (!model.contains(savedSearch)) {
                            ListUtil.insertSorted(model, savedSearch,
                                    ComparatorSavedSearch.INSTANCE,
                                    0, model.getSize() - 1);
                        }
                    } else {
                        MessageDisplayer.error(null,
                                "SavedSearchesModifier.Error.SearchCouldntBeSaved"); // NOI18N
                    }
                }
            });
        }
    }

    /**
     * Deletes a saved search after the user confirms.
     *
     * @param savedSearch  saved search
     */
    public static void delete(final SavedSearch savedSearch) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                String searchName = savedSearch.getParamStatement().getName();
                if (confirmDelete(searchName)) {
                    if (getDb().deleteSavedSearch(searchName)) {
                        ListModelSavedSearches model = getModel();
                        model.removeElement(savedSearch);
                    } else {
                        MessageDisplayer.error(null,
                                "SavedSearchesModifier.Error.SavedSearchCouldntBeDeleted"); // NOI18N
                    }
                }
            }
        });
    }

    /**
     * Renames saved search after the user confirms.
     *
     * @param oldSearch  saved search
     */
    public static void rename(final SavedSearch oldSearch) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                String oldName = oldSearch.getName();
                String newName = getNewName(oldName);
                if (newName != null) {
                    DatabaseSavedSearches db = getDb();
                    if (db.updateRenameSavedSearch(oldName, newName)) {
                        SavedSearch newSearch = db.getSavedSearch(newName);
                        if (newSearch == null) {
                            MessageDisplayer.error(null,
                                    "SavedSearchesModifier.Error.SavedSearchWasRenamedButCouldntBeLoaded", // NOI18N
                                    oldName);
                        } else {
                            getModel().rename(oldSearch, newSearch);
                        }
                    } else {
                        MessageDisplayer.error(null,
                                "SavedSearchesModifier.Error.RenameFailed", // NOI18N
                                oldName);
                    }
                }
            }
        });
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
        return (ListModelSavedSearches) GUI.INSTANCE.getAppPanel().
                getListSavedSearches().getModel();
    }

    private static String getInput(String oldName) {
        return JOptionPane.showInputDialog(
                null,
                Bundle.getString("SavedSearchesModifier.Input.NewName"), // NOI18N
                oldName);
    }

    private static boolean confirmInputDifferentName(String input) {
        return MessageDisplayer.confirm(
                GUI.INSTANCE.getAppPanel().getListSavedSearches(),
                "SavedSearchesModifier.Confirm.ChangeNameBecauseExists", // NOI18N
                MessageDisplayer.CancelButton.HIDE, input).equals(
                MessageDisplayer.ConfirmAction.YES);
    }

    private static boolean confirmDelete(String name) {
        return MessageDisplayer.confirm(
                GUI.INSTANCE.getAppPanel().getListSavedSearches(),
                "SavedSearchesModifier.Confirm.DeleteSearch", // NOI18N
                MessageDisplayer.CancelButton.HIDE, name).equals(
                MessageDisplayer.ConfirmAction.YES);
    }

    private static boolean confirmInsert(SavedSearch savedSearch) {
        if (getDb().existsSavedSearch(savedSearch)) {
            return MessageDisplayer.confirm(
                GUI.INSTANCE.getAppPanel().getListSavedSearches(),
                    "SavedSearchesModifier.Confirm.ReplaceExisting", // NOI18N
                    MessageDisplayer.CancelButton.HIDE).equals(
                    MessageDisplayer.ConfirmAction.YES);
        }
        return true;
    }

    private ModifySavedSearches() {
    }
}
