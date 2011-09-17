package org.jphototagger.program.comparator;

import java.util.Comparator;

import org.jphototagger.domain.metadata.keywords.Keyword;

/**
 * Compares {@code Keyword}s by their database ID.
 *
 * @author Elmar Baumann
 */
public final class KeywordIdAscendingComparator implements Comparator<Keyword> {

    public static final KeywordIdAscendingComparator INSTANCE = new KeywordIdAscendingComparator();

    @Override
    public int compare(Keyword o1, Keyword o2) {
        return NumberAscendingCompare.compare(o1.getId(), o2.getId());
    }
}
