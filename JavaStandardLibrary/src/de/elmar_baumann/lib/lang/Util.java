package de.elmar_baumann.lib.lang;

import java.io.PrintStream;

/**
 * Utils.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/31
 */
public class Util {

    /**
     * Liefert die in einem Byte gesetzten Bits.
     * 
     * @param b Byte
     * @return  true für jedes gesetzte Bit, false für jedes nicht gesetzte
     */
    public static boolean[] getBits(byte b) {
        boolean[] bits = new boolean[8];
        for (int i = 0; i < bits.length; i++) {
            bits[i] = ((b & (1 << i)) != 0);
        }
        return bits;
    }

    /**
     * Gibt das Bitmuster der Bytes aus, 0 für ein nicht gesetztes Bit und 1 für
     * ein gesetzts.
     * 
     * Die Bytes werden in der Array-Reihenfolge ausgegeben, die Bits jedes
     * Bytes von rechts nach links.
     * 
     * @param b   Bytearray
     * @param out Ausgabe
     */
    public static void dumpBits(byte[] b, PrintStream out) {
        out.println();
        for (int i = 0; i < b.length; i++) {
            boolean[] bits = Util.getBits(b[i]);
            for (int j = bits.length - 1; j >= 0; j--) {
                out.print(bits[j] ? "1" : "0"); // NOI18N
            }
            out.print(" "); // NOI18N
        }
    }
}
