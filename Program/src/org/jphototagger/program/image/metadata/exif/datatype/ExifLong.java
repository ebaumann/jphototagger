/*
 * @(#)ExifLong.java    Created on 2009-04-04
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

import org.jphototagger.program.image.metadata.exif.Ensure;

import java.nio.ByteOrder;

/**
 * EXIF data exifDataType LONG as described in the standard: A 32-bit (4-byte) unsigned
 * integer.
 *
 * BUGS: Possibly too small because the EXIF LONG is unsigned and has the
 * same byte count.
 *
 * @author  Elmar Baumann
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
    public ExifLong(byte[] rawValue, ByteOrder byteOrder) {
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

    public static ExifDataType dataType() {
        return ExifDataType.LONG;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ExifLong other = (ExifLong) obj;

        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        final int hash = 7;

        return 71 * hash + this.value;
    }
}
