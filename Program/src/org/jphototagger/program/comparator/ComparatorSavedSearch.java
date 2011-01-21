package org.jphototagger.program.comparator;

import org.jphototagger.program.data.SavedSearch;

import java.util.Comparator;

/**
 * Compares {@link org.jphototagger.program.data.SavedSearch} objects. Only the
 * names ({@link org.jphototagger.program.data.SavedSearch#getName()})
 * will be compared.
 *
 * @author Elmar Baumann
 */
public final class ComparatorSavedSearch implements Comparator<SavedSearch> {
    public static final ComparatorSavedSearch INSTANCE =
        new ComparatorSavedSearch();

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

    private ComparatorSavedSearch() {}
}
