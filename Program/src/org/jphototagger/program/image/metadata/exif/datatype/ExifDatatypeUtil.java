/*
 * @(#)ExifDatatypeUtil.java    Created on 2009-03-31
 *
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

package org.jphototagger.program.image.metadata.exif.datatype;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ExifDatatypeUtil {
    public static double toDouble(ExifRational rational) {
        double numerator   = rational.numerator();
        double denominator = rational.denominator();

        assert denominator > 0 : denominator;

        return numerator / denominator;
    }

    public static long toLong(ExifRational rational) {
        return (long) Math.floor(toDouble(rational));
    }

    public static int intFromRawValue(byte[] rawValue, ByteOrder byteOrder) {
        ByteBuffer buf = getByeBuffer(rawValue, byteOrder);

        return buf.getInt();
    }

    public static short shortFromRawValue(byte[] rawValue,
            ByteOrder byteOrder) {
        ByteBuffer buf = getByeBuffer(rawValue, byteOrder);

        return buf.getShort();
    }

    private static ByteBuffer getByeBuffer(byte[] rawValue,
            ByteOrder byteOrder) {
        ByteBuffer buf = ByteBuffer.wrap(rawValue);

        buf.order(byteOrder);

        return buf;
    }

    private ExifDatatypeUtil() {}
}
