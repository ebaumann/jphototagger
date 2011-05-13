package org.jphototagger.program.comparator;

import org.jphototagger.program.data.Keyword;
import java.util.Comparator;

/**
 * Compares {@link Keyword}s by the database ID of their parents.
 *
 * @author Elmar Baumann
 */
public final class ComparatorKeywordIdParent implements Comparator<Keyword> {
    public static final ComparatorKeywordIdParent INSTANCE = new ComparatorKeywordIdParent();

    @Override
    public int compare(Keyword o1, Keyword o2) {
        return NumberCompare.compare(o1.getIdParent(), o2.getIdParent());
    }
}
