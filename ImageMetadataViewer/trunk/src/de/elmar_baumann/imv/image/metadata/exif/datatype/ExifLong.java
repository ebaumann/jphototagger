package de.elmar_baumann.imv.image.metadata.exif.datatype;

/**
 * EXIF data type LONG as described in the standard: A 32-bit (4-byte) unsigned
 * integer.
 *
 * BUGS: Possibly too small because the EXIF LONG is unsigned and has the
 * same byte count.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/04/04
 */
public final class ExifLong {

    private final int value;

    /**
     * Creates a new instance.
     *
     * @param  rawValue   raw value
     * @param  byteOrder  byte order
     * @throws IllegalArgumentException if the length of the raw value is not
     *         equals to {@link #getRawValueByteCount()} or if the value is
     *         negativ
     */
    public ExifLong(byte[] rawValue, ExifByteOrder byteOrder) {

        if (!isRawValueByteCountOk(rawValue))
            throw new IllegalArgumentException("Illegal raw value count: " +
                    rawValue.length);

        value = ExifDatatypeUtil.intFromRawValue(rawValue, byteOrder);

        if (value < 0)
            throw new IllegalArgumentException("Negativ value: " + value);
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
        return rawValue.length == getRawValueByteCount();
    }

    public ExifType getDataTyp() {
        return ExifType.LONG;
    }

    /**
     * Returns the value.
     *
     * @return value {@code >= 0}
     */
    public int getValue() {
        return value;
    }
}
