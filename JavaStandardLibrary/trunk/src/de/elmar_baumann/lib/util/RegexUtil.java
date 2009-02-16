package de.elmar_baumann.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utils for regular expressions.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/02/16
 */
public final class RegexUtil {

    /**
     * Returns from a collection all strings matching a pattern.
     *
     * @param  pattern  pattern
     * @return All matching strings
     */
    public static List<String> getMatches(Collection<String> strings, String pattern) {
        if (strings == null)
            throw new NullPointerException("strings == null");
        if (pattern == null)
            throw new NullPointerException("pattern == null");

        List<String> stringsMatches = new ArrayList<String>();

        for (String string : strings) {
            if (string.matches(pattern)) {
                stringsMatches.add(string);
            }
        }
        return stringsMatches;
    }
}
