package de.elmar_baumann.lib.lang;

import java.io.PrintStream;
import java.nio.ByteBuffer;

/**
 * Utils.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/31
 */
public final class Util {

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
        if (b == null)
            throw new NullPointerException("b == null");
        if (out == null)
            throw new NullPointerException("out == null");

        out.println();
        for (int i = 0; i < b.length; i++) {
            boolean[] bits = Util.getBits(b[i]);
            for (int j = bits.length - 1; j >= 0; j--) {
                out.print(bits[j] ? "1" : "0"); // NOI18N
            }
            out.print(" "); // NOI18N
        }
    }

    /**
     * Compares two byte arrays.
     *
     * @param a1  first byte array
     * @param a2  second byte array
     *
     * @return    A negative integer, zero, or a positive integer as the first
     *            byte array is less than, equal to, or greater than the second
     *            byte array
     */
    public static int compareTo(byte[] a1, byte[] a2) {
        ByteBuffer buf1 = ByteBuffer.wrap(a1);
        ByteBuffer buf2 = ByteBuffer.wrap(a2);
        return buf1.compareTo(buf2);
    }

    private Util() {
    }
}
