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

import java.util.Arrays;

/**
 * EXIF data type RATIONAL as described in the standard: Two LONGs. The first
 * LONG is the numerator and the second LONG expresses the denominator.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-03-17
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
     *         equals to {@link #getRawValueByteCount()} or if the result is
     *         negativ or if the denominator is zero
     */
    public ExifRational(byte[] rawValue, ExifByteOrder byteOrder) {

        if (!isRawValueByteCountOk(rawValue))
            throw new IllegalArgumentException(
                    "Illegal raw value byte count: " + rawValue.length); // NOI18N

        numerator = ExifDatatypeUtil.intFromRawValue(
                Arrays.copyOfRange(rawValue, 0, 4),
                byteOrder);
        denominator = ExifDatatypeUtil.intFromRawValue(
                Arrays.copyOfRange(rawValue, 4, 8), byteOrder);

        if (isNegativ())
            throw new IllegalArgumentException("Negativ expression: " + // NOI18N
                    numerator + "/" + denominator); // NOI18N
        if (denominator == 0)
            throw new IllegalArgumentException("Illegal denominator: " + // NOI18N
                    denominator);
    }

    private boolean isNegativ() {
        return numerator < 0 && denominator > 0 || numerator > 0 &&
                denominator < 0;
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int getRawValueByteCount() {
        return 8;
    }

    public static boolean isRawValueByteCountOk(byte[] rawValue) {
        return rawValue.length == getRawValueByteCount();
    }

    /**
     * Returns the denominator.
     *
     * @return denominator {@code >= 0}
     */
    public int getDenominator() {
        return denominator;
    }

    /**
     * Returns the numerator.
     *
     * @return numerator {@code >= 0}
     */
    public int getNumerator() {
        return numerator;
    }

    public ExifType getDataTyp() {
        return ExifType.RATIONAL;
    }
}
