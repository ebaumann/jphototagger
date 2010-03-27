/*
 * @(#)ExifShort.java    Created on 2009-04-04
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
 * EXIF data exifDataType <code>SHORT</code> as defined in the EXIF standard:
 * A 16-bit (2-byte) unsigned integer.
 *
 * BUGS: Possibly too small because the EXIF SHORT is unsigned and has the
 * same byte count.
 *
 * @author  Elmar Baumann
 */
public final class ExifShort {
    private final short value;

    /**
     * Creates a new instance.
     *
     * @param  rawValue   raw value
     * @param  byteOrder  byte order
     * @throws IllegalArgumentException if the length of the raw value is not
     *         equals to {@link #byteCount()} or negativ
     */
    public ExifShort(byte[] rawValue, ByteOrder byteOrder) {
        Ensure.length(rawValue, byteCount());
        value = ExifDatatypeUtil.shortFromRawValue(rawValue, byteOrder);
        Ensure.positive(value);
    }

    /**
     * Returns the value.
     *
     * @return value
     */
    public short value() {
        return value;
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int byteCount() {
        return 2;
    }

    public static boolean byteCountOk(byte[] rawValue) {
        return rawValue.length == byteCount();
    }

    public static ExifDataType dataType() {
        return ExifDataType.SHORT;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ExifShort other = (ExifShort) obj;

        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        final int hash = 5;

        return 59 * hash + this.value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
