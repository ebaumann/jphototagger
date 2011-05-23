package org.jphototagger.program.image.metadata.exif.datatype;

import org.jphototagger.program.image.metadata.exif.Ensure;

/**
 * EXIF data exifDataType <code>BYTE</code> as defined in the EXIF standard:
 * An 8-bit unsigned integer.
 *
 *
 * @author Elmar Baumann
 */
public final class ExifByte {

    private final int value;

    /**
     * EXIF data exifDataType BYTE as described in the standard: An 8-bit unsigned
     * integer.
     *
     * @param  rawValue
     * @throws IllegalArgumentException if the raw value byte count is not
     *         equals to {@link #getRawValueByteCount()} or negative
     */
    public ExifByte(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        Ensure.length(rawValue, getRawValueByteCount());
        value = (int) rawValue[0];
        Ensure.zeroOrPositive(value);
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int getRawValueByteCount() {
        return 1;
    }

    public static boolean isRawValueByteCountOk(byte[] rawValue) {
        return rawValue == null
                ? false
                : rawValue.length == getRawValueByteCount();
        }

    public static ExifDataType getExifDataType() {
        return ExifDataType.BYTE;
    }

    public int getValue() {
        return value;
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

        if (!(obj instanceof ExifByte)) {
            return false;
        }

        ExifByte other = (ExifByte) obj;

        return value == other.value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.value;
        return hash;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
