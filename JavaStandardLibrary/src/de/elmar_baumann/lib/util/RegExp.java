package de.elmar_baumann.lib.util;

/**
 * Utils für reguläre Ausdrücke.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/11
 */
public class RegExp {

    /**
     * Maskiert in einem String Zeichen, die eine besondere Bedeutung haben
     * für reguläre Ausdrücke, z.B. wird jeder Backslash verdoppelt.
     * 
     * @param string String, der <em>keine</em> Zeichen mit besonderer Bedeutung
     *               enthält
     * @return       Maskierter String
     */
    public static String escape(String string) {
        // "\\", "\\\\" muss am Anfang stehen!
        return string.replace("\\", "\\\\"). // NOI18N
            replace("*", "\\*"). // NOI18N
            replace(".", "\\."); // NOI18N
    }
}
