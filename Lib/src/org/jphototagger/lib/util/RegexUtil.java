package org.jphototagger.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * Utils for regular expressions.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author Elmar Baumann
 */
public final class RegexUtil {

    /**
     * Returns from a collection all strings matching a pattern.
     *
     * @param  strings  strings
     * @param pattern   pattern
     * @return All matching strings
     */
    public static List<String> getMatches(Collection<String> strings, String pattern) {
        if (strings == null) {
            throw new NullPointerException("strings == null");
        }

        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }

        List<String> stringsMatches = new ArrayList<String>();

        for (String string : strings) {
            if (string.matches(pattern)) {
                stringsMatches.add(string);
            }
        }

        return stringsMatches;
    }

    /**
     * Returns, whether a string matches at least one pattern in a collection
     * with regular expressions.
     *
     * Uses <code>java.lang.String.matches(java.lang.String)</code>.
     *
     * @param  patterns  collection of string patterns
     * @param  string    string
     * @return true, if the string matches at least one pattern
     * @throws PatternSyntaxException if the syntax of the regular expression
     *         is invalid (as long as the elements in the list of string
     *         patterns don't match)
     */
    public static boolean containsMatch(Collection<String> patterns, String string) {
        if (patterns == null) {
            throw new NullPointerException("patterns == null");
        }

        if (string == null) {
            throw new NullPointerException("string == null");
        }

        for (String pattern : patterns) {
            if (string.matches(pattern)) {
                return true;
            }
        }

        return false;
    }

    private RegexUtil() {}
}
