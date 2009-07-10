package de.elmar_baumann.imv.comparator;

import de.elmar_baumann.imv.data.HierarchicalSubject;
import java.util.Comparator;

/**
 * Compares {@link HierarchicalSubject}s by the database ID of their parents.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/10
 */
public final class ComparatorHierarchicalSubjectIdParent
        implements Comparator<HierarchicalSubject> {

    public static final ComparatorHierarchicalSubjectIdParent INSTANCE =
            new ComparatorHierarchicalSubjectIdParent();

    @Override
    public int compare(HierarchicalSubject o1, HierarchicalSubject o2) {
        return NumberCompare.compare(o1.getIdParent(), o2.getIdParent());
    }
}
