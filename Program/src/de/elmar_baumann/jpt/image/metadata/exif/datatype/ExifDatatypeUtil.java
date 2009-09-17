/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.image.metadata.exif.datatype;

import java.nio.ByteBuffer;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-03-31
 */
public final class ExifDatatypeUtil {

    public static double toDouble(ExifRational rational) {
        double numerator = rational.getNumerator();
        double denominator = rational.getDenominator();
        assert denominator > 0 : denominator;
        return numerator / denominator;
    }

    public static long toLong(ExifRational rational) {
        return (long) Math.floor(toDouble(rational));
    }

    public static int intFromRawValue(byte[] rawValue, ExifByteOrder byteOrder) {
        ByteBuffer buf = getByeBuffer(rawValue, byteOrder);
        return buf.getInt();
    }

    public static short shortFromRawValue(byte[] rawValue,
            ExifByteOrder byteOrder) {
        ByteBuffer buf = getByeBuffer(rawValue, byteOrder);
        return buf.getShort();
    }

    private static ByteBuffer getByeBuffer(byte[] rawValue,
            ExifByteOrder byteOrder) {
        ByteBuffer buf = ByteBuffer.wrap(rawValue);
        buf.order(byteOrder.equals(ExifByteOrder.LITTLE_ENDIAN)
                  ? java.nio.ByteOrder.LITTLE_ENDIAN
                  : java.nio.ByteOrder.BIG_ENDIAN);
        return buf;
    }

    private ExifDatatypeUtil() {
    }
}
