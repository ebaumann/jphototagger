package de.elmar_baumann.imv.comparator;

import de.elmar_baumann.imv.data.SavedSearch;
import java.util.Comparator;

/**
 * Compares {@link de.elmar_baumann.imv.data.SavedSearch} objects. Only the
 * names ({@link de.elmar_baumann.imv.data.SavedSearch#getName()})
 * will be compared.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-05
 */
public final class ComparatorSavedSearch implements Comparator<SavedSearch> {

    public static final ComparatorSavedSearch INSTANCE =
            new ComparatorSavedSearch();

    @Override
    public int compare(SavedSearch o1, SavedSearch o2) {
        String nameO1 = o1.getParamStatement().getName();
        String nameO2 = o2.getParamStatement().getName();
        return o1 == o2 || nameO1 == null && nameO2 == null
               ? 0
               : nameO1 == null && nameO2 != null
                 ? -1
                 : nameO1 != null && nameO2 == null
                   ? 1
                   : nameO1.compareToIgnoreCase(nameO2);
    }

    private ComparatorSavedSearch() {
    }
}
