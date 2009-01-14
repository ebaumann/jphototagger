package de.elmar_baumann.lib.util;

import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * Utils für Arrays.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ArrayUtil {

    /**
     * Erzeugt aus einem String eine String-List.
     * 
     * @param string    String mit Token
     * @param delimiter Begrenzer zwischen den Token; jedes Zeichen ist
     *                  ein unterschiedlicher Begrenzer
     * @return          Einzelne Token
     */
    public static List<String> stringTokenToList(String string, String delimiter) {
        if (string == null)
            throw new NullPointerException("string == null");
        if (delimiter == null)
            throw new NullPointerException("delimiter == null");

        StringTokenizer tokenizer = new StringTokenizer(string, delimiter);
        List<String> list = new ArrayList<String>(tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }
        return list;
    }

    /**
     * Converts a string into an integer array. <em>It is expected, that
     * each token can be parsed as an integer!</em>
     * 
     * @param string    string
     * @param delimiter delimiter between integer token
     * @return          list
     * @throws          NumberFormatException if the string contains a not
     *                  parsable Integer
     */
    public static List<Integer> integerTokenToList(String string, String delimiter) {
        if (string == null)
            throw new NullPointerException("string == null");
        if (delimiter == null)
            throw new NullPointerException("delimiter == null");

        List<Integer> list = new ArrayList<Integer>();
        StringTokenizer tokenizer = new StringTokenizer(string, delimiter);
        while (tokenizer.hasMoreTokens()) {
            list.add(Integer.parseInt(tokenizer.nextToken()));
        }
        return list;
    }

    /**
     * Liefert von einem Objektarray einen Array mit den Strings, die
     * <code>toString()</code> der Objekte lieferte.
     * 
     * @param  array Objektarray
     * @return Stringarray
     */
    public static String[] toStringArray(Object[] array) {
        if (array == null)
            throw new NullPointerException("array == null");

        String[] sArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            Object o = array[i];
            sArray[i] = o == null ? "" : o.toString(); // NOI18N
        }
        return sArray;
    }

    /**
     * Returns, whether a string matches one ore more patterns in an array with
     * regular expressions.
     * 
     * Uses <code>java.lang.String.matches(java.lang.String)</code>
     * 
     * @param  patterns  patterns
     * @param  string    string
     * @return true, if the string matches at least one pattern
     */
    public static boolean matches(List<String> patterns, String string) {
        if (patterns == null)
            throw new NullPointerException("patterns == null");
        if (string == null)
            throw new NullPointerException("string == null");

        for (String pattern : patterns) {
            if (string.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    private ArrayUtil() {
    }
}
