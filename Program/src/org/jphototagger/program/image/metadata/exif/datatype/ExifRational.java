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
     *         equals to {@link #getRawValueByteCount()} or if the result is negative or if the denominator is zero
     */
    public ExifRational(byte[] rawValue, ByteOrder byteOrder) {
        Ensure.length(rawValue, getRawValueByteCount());
        numerator = ExifDatatypeUtil.convertRawValueToInt(Arrays.copyOfRange(rawValue, 0, 4), byteOrder);
        denominator = ExifDatatypeUtil.convertRawValueToInt(Arrays.copyOfRange(rawValue, 4, 8), byteOrder);
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

        if (rawValue.length == getRawValueByteCount()) {
            int numerator = ExifDatatypeUtil.convertRawValueToInt(Arrays.copyOfRange(rawValue, 0, 4), byteOrder);
            int denominator = ExifDatatypeUtil.convertRawValueToInt(Arrays.copyOfRange(rawValue, 4, 8), byteOrder);
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
    public static int getRawValueByteCount() {
        return 8;
    }

    public static boolean isRawValueByteCountOk(byte[] rawValue) {
        return rawValue == null
                ? false
                : rawValue.length == getRawValueByteCount();
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

    public static ExifDataType getExifDataType() {
        return ExifDataType.RATIONAL;
    }

    /**
     *
     * @param  obj
     * @return     true if thei numerators and denumerators of both objects are equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ExifRational)) {
            return false;
        }

        ExifRational other = (ExifRational) obj;

        return this.numerator == other.numerator && this.denominator == other.denominator;
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
