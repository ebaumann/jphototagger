/*
 * @(#)ExifRational.java    Created on 2009-03-17
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

import java.util.Arrays;

/**
 * EXIF data exifDataType RATIONAL as described in the standard: Two LONGs. The first
 * LONG is the numerator and the second LONG expresses the denominator.
 *
 * @author  Elmar Baumann
 * @see ExifLong
 */
public final class ExifRational {
    private final int numerator;
    private final int denominator;

    /**
     * Creates a new instance.
     *
     * @param  rawValue   raw value
     * @param  byteOrder  byte order
     * @throws IllegalArgumentException if the length of the raw value is not
     *         equals to {@link #byteCount()} or if the result is
     *         negativ or if the denominator is zero
     */
    public ExifRational(byte[] rawValue, ByteOrder byteOrder) {
        Ensure.length(rawValue, byteCount());
        numerator =
            ExifDatatypeUtil.intFromRawValue(Arrays.copyOfRange(rawValue, 0,
                4), byteOrder);
        denominator =
            ExifDatatypeUtil.intFromRawValue(Arrays.copyOfRange(rawValue, 4,
                8), byteOrder);
        Ensure.positive(numerator, denominator);
        Ensure.noDivisionByZero(denominator);
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int byteCount() {
        return 8;
    }

    public static boolean byteCountOk(byte[] rawValue) {
        return rawValue.length == byteCount();
    }

    /**
     * Returns the denominator.
     *
     * @return denominator {@code >= 0}
     */
    public int denominator() {
        return denominator;
    }

    /**
     * Returns the numerator.
     *
     * @return numerator {@code >= 0}
     */
    public int numerator() {
        return numerator;
    }

    public static ExifDataType dataType() {
        return ExifDataType.RATIONAL;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ExifRational other = (ExifRational) obj;

        return (this.numerator == other.numerator)
               && (this.denominator == other.denominator);
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 13 * hash + this.numerator;
        hash = 13 * hash + this.denominator;

        return hash;
    }

    @Override
    public String toString() {
        return Integer.toString(denominator) + "/"
               + Integer.toString(numerator);
    }
}
