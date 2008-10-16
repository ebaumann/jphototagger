package de.elmar_baumann.lib.util;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * Utils für Arrays.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ArrayUtil {

    /**
     * Erzeugt aus einem String eine String-List.
     * 
     * @param string    String mit Token
     * @param delimiter Begrenzer zwischen den Token
     * @return          List
     */
    public static List<String> stringTokenToList(String string,
        String delimiter) {
        return Arrays.asList(string.split(delimiter));
    }

    /**
     * Erzeugt aus einem String einen String-Vector.
     * 
     * @param string    String mit Token
     * @param delimiter Begrenzer zwischen den Token
     * @return          ArrayList
     */
    public static List<String> stringTokenToArray(String string, String delimiter) {
        List<String> array = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string, delimiter);
        while (tokenizer.hasMoreTokens()) {
            array.add(tokenizer.nextToken());
        }
        return array;
    }

    /**
     * Liefert von einem Objektarray einen Array mit den Strings, die
     * <code>toString()</code> der Objekte lieferte.
     * 
     * @param  array Objektarray
     * @return Stringarray
     */
    public static String[] toStringArray(Object[] array) {
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
        for (String pattern : patterns) {
            if (string.matches(pattern)) {
                return true;
            }
        }
        return false;
    }
}
