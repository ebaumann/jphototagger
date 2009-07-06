package de.elmar_baumann.imv.image.metadata.exif.datatype;

/**
 * EXIF data type <code>BYTE</code> as defined in the EXIF standard:
 * An 8-bit unsigned integer.
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/04/04
 */
public final class ExifByte {

    private final int value;

    /**
     * EXIF data type BYTE as described in the standard: An 8-bit unsigned
     * integer.
     * 
     * @param  rawValue  raw value
     * @throws IllegalArgumentException if the raw value byte count is not
     *         equals to {@link #getRawValueByteCount()} or negativ
     */
    public ExifByte(byte[] rawValue) {
        if (!isRawValueByteCountOk(rawValue))
            throw new IllegalArgumentException("Illegal raw value byte count: " + rawValue.length);
        value = (int) rawValue[0];
        if (value < 0)
            throw new IllegalArgumentException("Negativ value: " + value);
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
        return rawValue.length == getRawValueByteCount();
    }

    public ExifDataType getDataTyp() {
        return ExifDataType.BYTE;
    }

    public int getValue() {
        return value;
    }
}
