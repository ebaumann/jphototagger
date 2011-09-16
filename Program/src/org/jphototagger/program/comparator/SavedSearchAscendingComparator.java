package org.jphototagger.program.comparator;

import java.util.Comparator;

import org.jphototagger.domain.metadata.search.SavedSearch;

/**
 * Compares {@code org.jphototagger.program.data.SavedSearch} objects. Only the
 * names ({@code org.jphototagger.program.data.SavedSearch#getName()})
 * will be compared.
 *
 * @author Elmar Baumann
 */
public final class SavedSearchAscendingComparator implements Comparator<SavedSearch> {

    public static final SavedSearchAscendingComparator INSTANCE = new SavedSearchAscendingComparator();

    @Override
    public int compare(SavedSearch o1, SavedSearch o2) {
        String nameO1 = o1.getName();
        String nameO2 = o2.getName();

        return ((o1 == o2) || ((nameO1 == null) && (nameO2 == null)))
                ? 0
                : ((nameO1 == null) && (nameO2 != null))
                ? -1
                : ((nameO1 != null) && (nameO2 == null))
                ? 1
                : nameO1.compareToIgnoreCase(nameO2);
    }

    private SavedSearchAscendingComparator() {
    }
}
