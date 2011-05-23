package org.jphototagger.lib.util;

import java.io.PrintStream;
import java.nio.ByteBuffer;

/**
 *
 * @author Elmar Baumann
 */
public final class ByteUtil {
    public static int toInt(byte b) {
        int i = b;

        i &= 0x000000FF;

        return i;
    }

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
        if (b == null) {
            throw new NullPointerException("b == null");
        }

        if (out == null) {
            throw new NullPointerException("out == null");
        }

        out.println();

        for (int i = 0; i < b.length; i++) {
            boolean[] bits = getBits(b[i]);

            for (int j = bits.length - 1; j >= 0; j--) {
                out.print(bits[j]
                          ? "1"
                          : "0");
            }

            out.print(" ");
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
        if (a1 == null) {
            throw new NullPointerException("a1 == null");
        }

        if (a2 == null) {
            throw new NullPointerException("a2 == null");
        }

        ByteBuffer buf1 = ByteBuffer.wrap(a1);
        ByteBuffer buf2 = ByteBuffer.wrap(a2);

        return buf1.compareTo(buf2);
    }

    /**
     * 
     * @param  a1 can be null
     * @param  a2 can be null
     * @return 
     */
    public static boolean equals(byte[] a1, byte[] a2) {
        if (a1 == null && a2 == null) {
            return true;
        }
        
        if (a1 == null || a2 == null) {
            return false;
        }
        
        if (a1.length != a2.length) {
            return false;
        }
        
        for (int i = 0; i < a1.length; i++) {
            byte b1 = a1[i];
            byte b2 = a2[i];
            
            if (b1 != b2) {
                return false;
            }
        }
        
        return true;
    }

    private ByteUtil() {}
}
