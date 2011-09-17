package org.jphototagger.program.comparator;

import java.util.Comparator;

import org.jphototagger.domain.metadata.keywords.Keyword;

/**
 * Compares {@code Keyword}s by the database ID of their parents.
 *
 * @author Elmar Baumann
 */
public final class ParentKeywordIdAscendingComparator implements Comparator<Keyword> {

    public static final ParentKeywordIdAscendingComparator INSTANCE = new ParentKeywordIdAscendingComparator();

    @Override
    public int compare(Keyword o1, Keyword o2) {
        return NumberAscendingCompare.compare(o1.getIdParent(), o2.getIdParent());
    }
}
