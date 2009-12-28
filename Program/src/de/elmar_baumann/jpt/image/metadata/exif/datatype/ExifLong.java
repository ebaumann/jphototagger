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

import de.elmar_baumann.jpt.image.metadata.exif.Ensure;

/**
 * EXIF data type LONG as described in the standard: A 32-bit (4-byte) unsigned
 * integer.
 *
 * BUGS: Possibly too small because the EXIF LONG is unsigned and has the
 * same byte count.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-04-04
 */
public final class ExifLong {

    private final int value;

    /**
     * Creates a new instance.
     *
     * @param  rawValue   raw value
     * @param  byteOrder  byte order
     * @throws IllegalArgumentException if the length of the raw value is not
     *         equals to {@link #byteCount()} or if the value is
     *         negativ
     */
    public ExifLong(byte[] rawValue, ExifByteOrder byteOrder) {

        Ensure.length(rawValue, byteCount());

        value = ExifDatatypeUtil.intFromRawValue(rawValue, byteOrder);

        Ensure.positive(value);
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int byteCount() {
        return 4;
    }

    public static boolean byteCountOk(byte[] rawValue) {
        return rawValue.length == byteCount();
    }

    public static ExifType dataType() {
        return ExifType.LONG;
    }

    /**
     * Returns the value.
     *
     * @return value {@code >= 0}
     */
    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
