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
     * @param  rawValue   raw value
     * @param  byteOrder  byte order
     * @throws IllegalArgumentException if the length of the raw value is not
     *         equals to {@link #byteCount()} or negativ
     */
    public ExifShort(byte[] rawValue, ByteOrder byteOrder) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }

        Ensure.length(rawValue, byteCount());
        value = ExifDatatypeUtil.shortFromRawValue(rawValue, byteOrder);
        Ensure.zeroOrPositive(value);
    }

    /**
     * Returns the value.
     *
     * @return value
     */
    public short value() {
        return value;
    }

    public static boolean isZeroOrPositive(byte[] rawValue, ByteOrder byteOrder) {
        return rawValue.length == byteCount()
                && ExifDatatypeUtil.shortFromRawValue(rawValue, byteOrder) >= 0;
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
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

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
