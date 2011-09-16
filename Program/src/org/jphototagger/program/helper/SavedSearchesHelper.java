package org.jphototagger.program.helper;

import javax.swing.DefaultListModel;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.domain.repository.SavedSearchesRepository;
import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.controller.thumbnail.SortThumbnailsController;
import org.jphototagger.program.resource.GUI;

/**
 *
 * @author Elmar Baumann
 */
public final class SavedSearchesHelper {

    /**
     * Does request the focus to the list of saved searches within the
     * application's panel.
     */
    public static void focusAppPanelList() {
        GUI.getAppPanel().getListSavedSearches().requestFocus();
    }

    /**
     * Returns the index of a saved search through its name (primary key).
     *
     * @param  model model containing that saved search
     * @param  name  name of the saved search
     * @return       index or -1 if that model has no saved search with that
     *               name
     */
    public static int getIndexOfSavedSearch(DefaultListModel model, String name) {
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
            SavedSearchesRepository repo = Lookup.getDefault().lookup(SavedSearchesRepository.class);

            if (repo.saveSavedSearch(savedSearch)) {
                return true;
            } else {
                String message = Bundle.getString(SavedSearchesHelper.class, "SavedSearchesHelper.Error.Insert", savedSearch);
                MessageDisplayer.error(null, message);
            }
        }

        return false;
    }

    /**
     * Inserts a saved search and displays errors.
     *
     * @param  savedSearch saved search to updateSavedSearch
     * @return             true if updated
     */
    public static boolean update(SavedSearch savedSearch) {
        if (savedSearch == null) {
            throw new NullPointerException("savedSearch == null");
        }

        SavedSearchesRepository repo = Lookup.getDefault().lookup(SavedSearchesRepository.class);

        if (repo.updateSavedSearch(savedSearch)) {
            return true;
        } else {
            String message = Bundle.getString(SavedSearchesHelper.class, "SavedSearchesHelper.Error.Update", savedSearch);
            MessageDisplayer.error(null, message);

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
            SavedSearchesRepository repo = Lookup.getDefault().lookup(SavedSearchesRepository.class);

            if (!repo.deleteSavedSearch(searchName)) {
                String message = Bundle.getString(SavedSearchesHelper.class, "SavedSearchesHelper.Error.Delete");
                MessageDisplayer.error(null, message);
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
        String toName = getNotExistingName(fromName);

        if (toName != null) {
            SavedSearchesRepository repo = Lookup.getDefault().lookup(SavedSearchesRepository.class);

            if (!repo.updateRenameSavedSearch(fromName, toName)) {
                String message = Bundle.getString(SavedSearchesHelper.class, "SavedSearchesHelper.Error.Rename", fromName);
                MessageDisplayer.error(null, message);
            }
        }
    }

    /**
     * Calls {@code ThumbnailsPanel#setFileSortComparator(java.util.Comparator)}
     * with no sort order if the search uses custom SQL or the last used sort
     * order.
     *
     * @param search saved search
     */
    public static void setSort(SavedSearch search) {
        if (search == null) {
            throw new NullPointerException("search == null");
        }

        if (search.isCustomSql()) {
            GUI.getThumbnailsPanel().setFileSortComparator(FileSort.NO_SORT.getComparator());
            GUI.getAppFrame().selectMenuItemUnsorted();
        } else {
            SortThumbnailsController.setLastSort();
        }
    }

    /**
     * Returns a not existing search name via user input.
     *
     * @param  suggestName name in the input text field
     * @return             not existing name or null
     */
    public static String getNotExistingName(String suggestName) {
        if (suggestName == null) {
            throw new NullPointerException("suggestName == null");
        }

        boolean wantInput = true;
        String input = null;
        String suggest = suggestName;
        SavedSearchesRepository repo = Lookup.getDefault().lookup(SavedSearchesRepository.class);

        while (wantInput) {
            wantInput = false;
            input = getInput(suggest);

            if ((input != null) && repo.existsSavedSearch(input)) {
                wantInput = confirmInputNotExistingName(input);

                if (wantInput) {
                    suggest = input;
                }

                input = null;
            }
        }

        return input;
    }

    private static String getInput(String fromName) {
        String info = Bundle.getString(SavedSearchesHelper.class, "SavedSearchesHelper.Input.NewName");
        String input = fromName;

        return MessageDisplayer.input(info, input);
    }

    private static boolean confirmInputNotExistingName(String input) {
        String message = Bundle.getString(SavedSearchesHelper.class, "SavedSearchesHelper.Confirm.InputDifferentName", input);

        return MessageDisplayer.confirmYesNo(null, message);
    }

    private static boolean confirmDelete(String name) {
        String message = Bundle.getString(SavedSearchesHelper.class, "SavedSearchesHelper.Confirm.Delete", name);

        return MessageDisplayer.confirmYesNo(null, message);
    }

    private static boolean ensureNotOverwrite(String name) {
        SavedSearchesRepository repo = Lookup.getDefault().lookup(SavedSearchesRepository.class);

        if (repo.existsSavedSearch(name)) {
            String message = Bundle.getString(SavedSearchesHelper.class, "SavedSearchesHelper.Error.Exists", name);
            MessageDisplayer.error(null, message);

            return false;
        }

        return true;
    }

    private SavedSearchesHelper() {
    }
}
