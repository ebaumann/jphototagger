/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.lib.util;

import java.io.PrintStream;

import java.nio.ByteBuffer;

/**
 *
 * @author  Elmar Baumann
 * @version 2010-02-11
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
        ByteBuffer buf1 = ByteBuffer.wrap(a1);
        ByteBuffer buf2 = ByteBuffer.wrap(a2);

        return buf1.compareTo(buf2);
    }

    private ByteUtil() {}
}
