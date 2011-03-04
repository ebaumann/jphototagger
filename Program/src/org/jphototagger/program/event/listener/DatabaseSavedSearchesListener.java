package org.jphototagger.program.event.listener;

import org.jphototagger.program.data.SavedSearch;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface DatabaseSavedSearchesListener {
    void searchInserted(SavedSearch savedSearch);

    void searchUpdated(SavedSearch savedSearch);

    void searchDeleted(String name);

    void searchRenamed(String fromName, String toName);
}
