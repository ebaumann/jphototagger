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
 * @author Elmar Baumann
 */
public final class ExifLong {

    private final int value;

    /**
     *
     * @param  rawValue
     * @param  byteOrder
     * @throws IllegalArgumentException if the length of the raw value is not
     *         equals to {@link #getRawValueByteCount()} or if the value is negative
     */
    public ExifLong(byte[] rawValue, ByteOrder byteOrder) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }

        Ensure.length(rawValue, getRawValueByteCount());
        value = ExifDatatypeUtil.convertRawValueToInt(rawValue, byteOrder);
        Ensure.zeroOrPositive(value);
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int getRawValueByteCount() {
        return 4;
    }

    public static boolean isRawValueByteCountOk(byte[] rawValue) {
        return rawValue == null
                ? false
                : rawValue.length == getRawValueByteCount();
        }

    public static ExifDataType getExifDataType() {
        return ExifDataType.LONG;
    }

    /**
     *
     * @return value {@code >= 0}
     */
    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    /**
     *
     * @param  obj
     * @return     true if the values of both objects are equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ExifLong)) {
            return false;
        }

        ExifLong other = (ExifLong) obj;

        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        final int hash = 7;

        return 71 * hash + this.value;
    }
}
