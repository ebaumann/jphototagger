package org.jphototagger.domain.repository.event.search;

/**
 * @author Elmar Baumann
 */
public final class SavedSearchDeletedEvent {

    private final Object source;
    private final String searchName;

    public SavedSearchDeletedEvent(Object source, String searchName) {
        if (searchName == null) {
            throw new NullPointerException("searchName == null");
        }

        this.source = source;
        this.searchName = searchName;
    }

    public String getSearchName() {
        return searchName;
    }

    public Object getSource() {
        return source;
    }
}
