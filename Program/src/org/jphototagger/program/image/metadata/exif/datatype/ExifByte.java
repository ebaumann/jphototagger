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
     * @param  rawValue  raw value
     * @throws IllegalArgumentException if the raw value byte count is not
     *         equals to {@link #byteCount()} or negativ
     */
    public ExifByte(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        Ensure.length(rawValue, byteCount());
        value = (int) rawValue[0];
        Ensure.zeroOrPositive(value);
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
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

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
