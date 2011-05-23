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
 * @author Elmar Baumann
 */
public final class ExifShort {

    private final short value;

    /**
     * Creates a new instance.
     *
     * @param  rawValue
     * @param  byteOrder
     * @throws IllegalArgumentException if the length of the raw value is not
     *         equals to {@link #getRawValueByteCount()} or negative
     */
    public ExifShort(byte[] rawValue, ByteOrder byteOrder) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }

        Ensure.length(rawValue, getRawValueByteCount());
        value = ExifDatatypeUtil.convertRawValueToShort(rawValue, byteOrder);
        Ensure.zeroOrPositive(value);
    }

    /**
     * Returns the value.
     *
     * @return value
     */
    public short getValue() {
        return value;
    }

    public static boolean isRawValueZeroOrPositive(byte[] rawValue, ByteOrder byteOrder) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
    }

        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }

        return (rawValue.length == getRawValueByteCount()) && (ExifDatatypeUtil.convertRawValueToShort(rawValue, byteOrder) >= 0);
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int getRawValueByteCount() {
        return 2;
    }

    public static boolean isRawValueByteCountOk(byte[] rawValue) {
        return rawValue == null
                ? false
                : rawValue.length == getRawValueByteCount();
        }

    public static ExifDataType getExifDataType() {
        return ExifDataType.SHORT;
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

        if (!(obj instanceof ExifShort)) {
            return false;
        }

        ExifShort other = (ExifShort) obj;

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
