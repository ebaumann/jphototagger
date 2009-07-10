package de.elmar_baumann.imv.comparator;

import de.elmar_baumann.imv.data.HierarchicalSubject;
import java.util.Comparator;

/**
 * Compares {@link HierarchicalSubject}s by their database ID.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/10
 */
public final class ComparatorHierarchicalSubjectId
        implements Comparator<HierarchicalSubject> {

    public static ComparatorHierarchicalSubjectId INSTANCE =
            new ComparatorHierarchicalSubjectId();

    @Override
    public int compare(HierarchicalSubject o1, HierarchicalSubject o2) {
        return NumberCompare.compare(o1.getId(), o2.getId());
    }
}
