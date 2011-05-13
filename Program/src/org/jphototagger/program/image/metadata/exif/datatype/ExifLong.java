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
     * Creates a new instance.
     *
     * @param  rawValue   raw value
     * @param  byteOrder  byte order
     * @throws IllegalArgumentException if the length of the raw value is not
     *         equals to {@link #byteCount()} or if the value is
     *         negativ
     */
    public ExifLong(byte[] rawValue, ByteOrder byteOrder) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }

        Ensure.length(rawValue, byteCount());
        value = ExifDatatypeUtil.intFromRawValue(rawValue, byteOrder);
        Ensure.zeroOrPositive(value);
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
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

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
