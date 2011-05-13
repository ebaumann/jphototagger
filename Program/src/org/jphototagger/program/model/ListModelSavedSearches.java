package org.jphototagger.program.model;

import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseSavedSearches;
import org.jphototagger.program.event.listener.DatabaseSavedSearchesListener;
import org.jphototagger.program.helper.SavedSearchesHelper;
import java.util.List;
import javax.swing.DefaultListModel;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Elements are {@link SavedSearch}es.
 *
 * @author Elmar Baumann
 */
public final class ListModelSavedSearches extends DefaultListModel implements DatabaseSavedSearchesListener {
    private static final long serialVersionUID = 1979666986802551310L;

    public ListModelSavedSearches() {
        addElements();
        DatabaseSavedSearches.INSTANCE.addListener(this);
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        List<SavedSearch> savedSearches = DatabaseSavedSearches.INSTANCE.getAll();

        for (SavedSearch savedSearch : savedSearches) {
            addElement(savedSearch);
        }
    }

    private void insertSavedSearch(SavedSearch search) {
        if (!contains(search)) {
            addElement(search);
        }
    }

    private void deleteSearch(String name) {
        int index = SavedSearchesHelper.getIndexOfSavedSearch(this, name);

        if (index >= 0) {
            remove(index);
        }
    }

    private void renameSearch(String fromName, String toName) {
        int index = SavedSearchesHelper.getIndexOfSavedSearch(this, fromName);

        if (index >= 0) {
            SavedSearch savedSearch = (SavedSearch) get(index);

            savedSearch.setName(toName);
            fireContentsChanged(this, index, index);
        }
    }

    private void insertSearch(SavedSearch savedSearch) {
        insertSavedSearch(savedSearch);
    }

    private void updateSearch(SavedSearch savedSearch) {
        int index = indexOf(savedSearch);

        if (index >= 0) {
            set(index, savedSearch);
        } else {
            insertSavedSearch(savedSearch);
        }
    }

    @Override
    public void searchInserted(final SavedSearch savedSearch) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                insertSearch(savedSearch);
            }
        });
    }

    @Override
    public void searchUpdated(final SavedSearch savedSearch) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                updateSearch(savedSearch);
            }
        });
    }

    @Override
    public void searchDeleted(final String name) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                deleteSearch(name);
            }
        });
    }

    @Override
    public void searchRenamed(final String fromName, final String toName) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                renameSearch(fromName, toName);
            }
        });
    }
}
