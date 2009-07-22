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

        assert o1.getParamStatement() != null : "Null statement: " + o1;
        assert o2.getParamStatement() != null : "Null statement: " + o2;
        assert o1.getParamStatement().getName() != null : "Null name: " + o1;
        assert o2.getParamStatement().getName() != null : "Null name: " + o2;

        return o1.getParamStatement().getName().compareToIgnoreCase(
                o2.getParamStatement().getName());
    }

    private ComparatorSavedSearch() {
    }
}
