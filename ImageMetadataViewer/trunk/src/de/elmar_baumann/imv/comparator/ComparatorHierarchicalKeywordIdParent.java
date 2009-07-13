package de.elmar_baumann.imv.comparator;

import de.elmar_baumann.imv.data.HierarchicalKeyword;
import java.util.Comparator;

/**
 * Compares {@link HierarchicalKeyword}s by the database ID of their parents.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/10
 */
public final class ComparatorHierarchicalKeywordIdParent
        implements Comparator<HierarchicalKeyword> {

    public static final ComparatorHierarchicalKeywordIdParent INSTANCE =
            new ComparatorHierarchicalKeywordIdParent();

    @Override
    public int compare(HierarchicalKeyword o1, HierarchicalKeyword o2) {
        return NumberCompare.compare(o1.getIdParent(), o2.getIdParent());
    }
}
