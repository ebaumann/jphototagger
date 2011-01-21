package org.jphototagger.program.comparator;

import org.jphototagger.program.data.Keyword;

import java.util.Comparator;

/**
 * Compares {@link Keyword}s by their database ID.
 *
 * @author Elmar Baumann
 */
public final class ComparatorKeywordId implements Comparator<Keyword> {
    public static final ComparatorKeywordId INSTANCE = new ComparatorKeywordId();

    @Override
    public int compare(Keyword o1, Keyword o2) {
        return NumberCompare.compare(o1.getId(), o2.getId());
    }
}
