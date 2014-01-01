package org.jphototagger.domain.repository.event.search;

import org.jphototagger.domain.metadata.search.SavedSearch;

/**
 * @author Elmar Baumann
 */
public final class SavedSearchUpdatedEvent {

    private final Object source;
    private final SavedSearch savedSearch;

    public SavedSearchUpdatedEvent(Object source, SavedSearch savedSearch) {
        if (savedSearch == null) {
            throw new NullPointerException("savedSearch == null");
        }

        this.source = source;
        this.savedSearch = savedSearch;
    }

    public SavedSearch getSavedSearch() {
        return savedSearch;
    }

    public Object getSource() {
        return source;
    }
}
