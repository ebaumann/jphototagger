/*
 * @(#)SavedSearchesHelper.java    Created on 2009-02-25
 *
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

package org.jphototagger.program.helper;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.database.DatabaseSavedSearches;
import org.jphototagger.program.resource.GUI;

import javax.swing.DefaultListModel;

/**
 *
 * @author  Elmar Baumann
 */
public final class SavedSearchesHelper {

    /**
     * Does request the focus to the list of saved searches within the
     * application's panel.
     */
    public static void focusAppPanelList() {
        GUI.INSTANCE.getAppPanel().getListSavedSearches().requestFocus();
    }

    /**
     * Returns the index of a saved search through its name (primary key).
     *
     * @param  model model containing that saved search
     * @param  name  name of the saved search
     * @return       index or -1 if that model has no saved search with that
     *               name
     */
    public static int getIndexOfSavedSearch(DefaultListModel model,
            String name) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }

        if (name == null) {
            throw new NullPointerException("name == null");
        }

        int size = model.size();

        for (int i = 0; i < size; i++) {
            Object element = model.get(i);

            if (element instanceof SavedSearch) {
                SavedSearch savedSearch = (SavedSearch) element;

                if (savedSearch.getName().equals(name)) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Inserts a saved search and displays errors.
     *
     * @param  savedSearch saved search
     * @return             true if saved
     */
    public static boolean insert(SavedSearch savedSearch) {
        if (savedSearch == null) {
            throw new NullPointerException("savedSearch == null");
        }

        if (ensureNotOverwrite(savedSearch.getName())) {
            if (DatabaseSavedSearches.INSTANCE.insert(savedSearch)) {
                return true;
            } else {
                MessageDisplayer.error(null,
                                       "SavedSearchesHelper.Error.Insert",
                                       savedSearch);
            }
        }
        return false;
    }

    /**
     * Inserts a saved search and displays errors.
     *
     * @param  savedSearch saved search to update
     * @return             true if updated
     */
    public static boolean update(SavedSearch savedSearch) {
        if (savedSearch == null) {
            throw new NullPointerException("savedSearch == null");
        }

        if (DatabaseSavedSearches.INSTANCE.update(savedSearch)) {
            return true;
        } else {
            MessageDisplayer.error(null, "SavedSearchesHelper.Error.Update",
                                   savedSearch);
            return false;
        }
    }

    /**
     * Deletes a saved search if the user confirms.
     *
     * @param savedSearch saved search
     */
    public static void delete(SavedSearch savedSearch) {
        if (savedSearch == null) {
            throw new NullPointerException("savedSearch == null");
        }

        String searchName = savedSearch.getName();

        if (confirmDelete(searchName)) {
            if (!DatabaseSavedSearches.INSTANCE.delete(searchName)) {
                MessageDisplayer.error(null,
                                       "SavedSearchesHelper.Error.Delete");
            }
        }
    }

    /**
     * Renames saved search via user input.
     *
     * @param savedSearch  saved search
     */
    public static void rename(final SavedSearch savedSearch) {
        if (savedSearch == null) {
            throw new NullPointerException("oldSearch == null");
        }

        if (!savedSearch.isValid()) {
            assert false : savedSearch;

            return;
        }

        String fromName = savedSearch.getName();
        String toName   = getNotExistingName(fromName);

        if (toName != null) {
            if (!DatabaseSavedSearches.INSTANCE.updateRename(fromName,
                    toName)) {
                MessageDisplayer.error(null,
                                       "SavedSearchesHelper.Error.Rename",
                                       fromName);
            }
        }
    }

    /**
     * Returns a not existing search name via user input.
     *
     * @param  suggestName name in the input text field
     * @return             not existing name or null
     */
    public static String getNotExistingName(String suggestName) {
        boolean wantInput = true;
        String  input     = null;

        while (wantInput) {
            wantInput = false;
            input     = getInput(suggestName);

            if ((input != null)
                    && DatabaseSavedSearches.INSTANCE.exists(input)) {
                wantInput = confirmInputNotExistingName(input);

                if (wantInput) {
                    suggestName = input;
                }

                input = null;
            }
        }

        return input;
    }

    private static String getInput(String fromName) {
        return MessageDisplayer.input("SavedSearchesHelper.Input.NewName",
                                      fromName,
                                      SavedSearchesHelper.class.getName());
    }

    private static boolean confirmInputNotExistingName(String input) {
        return MessageDisplayer.confirmYesNo(null,
                "SavedSearchesHelper.Confirm.InputDifferentName", input);
    }

    private static boolean confirmDelete(String name) {
        return MessageDisplayer.confirmYesNo(null,
                "SavedSearchesHelper.Confirm.Delete", name);
    }

    private static boolean ensureNotOverwrite(String name) {
        if (DatabaseSavedSearches.INSTANCE.exists(name)) {
            MessageDisplayer.error(null, "SavedSearchesHelper.Error.Exists",
                                   name);

            return false;
        }

        return true;
    }

    private SavedSearchesHelper() {}
}
