package de.elmar_baumann.lib.util;

import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * Utils für Arrays.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/30
 */
public class ArrayUtil {

    /**
     * Erzeugt aus einem String einer String-ArrayList.
     * 
     * @param string    String mit Token
     * @param delimiter Begrenzer zwischen den Token
     * @return          ArrayList
     */
    public static ArrayList<String> stringTokenToArray(String string,
        String delimiter) {
        ArrayList<String> array = new ArrayList<String>();
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
}
