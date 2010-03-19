/*
 * @(#)ExifByte.java    Created on 2009-04-04
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

package de.elmar_baumann.jpt.image.metadata.exif.datatype;

import de.elmar_baumann.jpt.image.metadata.exif.Ensure;

/**
 * EXIF data exifDataType <code>BYTE</code> as defined in the EXIF standard:
 * An 8-bit unsigned integer.
 *
 *
 * @author  Elmar Baumann
 */
public final class ExifByte {
    private final int value;

    /**
     * EXIF data exifDataType BYTE as described in the standard: An 8-bit unsigned
     * integer.
     *
     * @param  rawValue  raw value
     * @throws IllegalArgumentException if the raw value byte count is not
     *         equals to {@link #byteCount()} or negativ
     */
    public ExifByte(byte[] rawValue) {
        Ensure.length(rawValue, byteCount());
        value = (int) rawValue[0];
        Ensure.positive(value);
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int byteCount() {
        return 1;
    }

    public static boolean byteCountOk(byte[] rawValue) {
        return rawValue.length == byteCount();
    }

    public static ExifDataType dataType() {
        return ExifDataType.BYTE;
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
