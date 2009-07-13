package de.elmar_baumann.imv.comparator;

import de.elmar_baumann.imv.data.HierarchicalKeyword;
import java.util.Comparator;

/**
 * Compares {@link HierarchicalKeyword}s by their database ID.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/10
 */
public final class ComparatorHierarchicalKeywordId
        implements Comparator<HierarchicalKeyword> {

    public static ComparatorHierarchicalKeywordId INSTANCE =
            new ComparatorHierarchicalKeywordId();

    @Override
    public int compare(HierarchicalKeyword o1, HierarchicalKeyword o2) {
        return NumberCompare.compare(o1.getId(), o2.getId());
    }
}
