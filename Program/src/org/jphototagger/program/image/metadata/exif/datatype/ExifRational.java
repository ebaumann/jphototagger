package org.jphototagger.program.image.metadata.exif.datatype;

import org.jphototagger.program.image.metadata.exif.Ensure;

import java.nio.ByteOrder;

import java.util.Arrays;

/**
 * EXIF data exifDataType RATIONAL as described in the standard: Two LONGs.
 * The first LONG is the numerator and the second LONG expresses the
 * denominator.
 *
 * @author Elmar Baumann
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
        numerator = ExifDatatypeUtil.intFromRawValue(Arrays.copyOfRange(rawValue, 0, 4), byteOrder);
        denominator = ExifDatatypeUtil.intFromRawValue(Arrays.copyOfRange(rawValue, 4, 8), byteOrder);
        Ensure.zeroOrPositive(numerator, denominator);
        Ensure.noDivisionByZero(denominator);
    }

    /**
     * Returns whether an byte array can be used to construct a valid
     * ExifRational object.
     *
     * @param rawValue  raw value
     * @param byteOrder byte order
     * @return          true if the bytes can be used to construct an
     *                  ExifRational object
     */
    public static boolean isValid(byte[] rawValue, ByteOrder byteOrder) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }

        if (rawValue.length == byteCount()) {
            int numerator = ExifDatatypeUtil.intFromRawValue(Arrays.copyOfRange(rawValue, 0, 4), byteOrder);
            int denominator = ExifDatatypeUtil.intFromRawValue(Arrays.copyOfRange(rawValue, 4, 8), byteOrder);
            boolean negative = ((numerator < 0) && (denominator > 0)) || ((numerator > 0) && (denominator < 0));

            return !negative && (denominator != 0);
        }

        return false;
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
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

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

        return (this.numerator == other.numerator) && (this.denominator == other.denominator);
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
        return Integer.toString(denominator) + "/" + Integer.toString(numerator);
    }
}
