/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.comparator.ComparatorSavedSearch;
import de.elmar_baumann.jpt.data.SavedSearch;
import de.elmar_baumann.jpt.database.DatabaseSavedSearches;
import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.model.ListModelSavedSearches;
import de.elmar_baumann.lib.componentutil.ListUtil;
import javax.swing.SwingUtilities;

/**
 * Inserts, updates, deletes  saved searches into/from the model and database.
 *
 * @author  Elmar Baumann
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
                    if (getDb().insertOrUpdate(savedSearch)) {
                        ListModelSavedSearches model = getModel();
                        if (!model.contains(savedSearch)) {
                            ListUtil.insertSorted(model, savedSearch,
                                    ComparatorSavedSearch.INSTANCE,
                                    0, model.getSize() - 1);
                        }
                    } else {
                        MessageDisplayer.error(null, "ModifySavedSearches.Error.SearchCouldntBeSaved");
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
                    if (getDb().delete(searchName)) {
                        ListModelSavedSearches model = getModel();
                        model.removeElement(savedSearch);
                    } else {
                        MessageDisplayer.error(null, "ModifySavedSearches.Error.SavedSearchCouldntBeDeleted");
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
                    if (db.updateRename(oldName, newName)) {
                        SavedSearch newSearch = db.find(newName);
                        if (newSearch == null) {
                            MessageDisplayer.error(null, "ModifySavedSearches.Error.SavedSearchWasRenamedButCouldntBeLoaded", oldName);
                        } else {
                            getModel().rename(oldSearch, newSearch);
                        }
                    } else {
                        MessageDisplayer.error(null, "ModifySavedSearches.Error.RenameFailed", oldName);
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
            if (input != null && getDb().exists(input)) {
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
        return ModelFactory.INSTANCE.getModel(ListModelSavedSearches.class);
    }

    private static String getInput(String oldName) {
        return MessageDisplayer.input("ModifySavedSearches.Input.NewName", oldName, ModifySavedSearches.class.getName());
    }

    private static boolean confirmInputDifferentName(String input) {
        return MessageDisplayer.confirmYesNo(null, "ModifySavedSearches.Confirm.ChangeNameBecauseExists", input);
    }

    private static boolean confirmDelete(String name) {
        return MessageDisplayer.confirmYesNo(null, "ModifySavedSearches.Confirm.DeleteSearch", name);
    }

    private static boolean confirmInsert(SavedSearch savedSearch) {
        if (getDb().exists(savedSearch)) {
            return MessageDisplayer.confirmYesNo(null, "ModifySavedSearches.Confirm.ReplaceExisting");
        }
        return true;
    }

    private ModifySavedSearches() {
    }
}
