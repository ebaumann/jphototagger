package de.elmar_baumann.imv.image.metadata.exif;

/**
 * EXIF data type LONG as described in the standard: A 32-bit (4-byte) unsigned
 * integer.
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
     *         equals to 4 or if the value is smaller than zero
     */
    public ExifLong(byte[] rawValue, ExifMetadata.ByteOrder byteOrder) {

        if (rawValue.length != 4)
            throw new IllegalArgumentException("rawValue != 4: " + rawValue);

        value = ExifGpsUtil.intFromRawValue(rawValue, byteOrder);

        if (value < 0)
            throw new IllegalArgumentException("value < 0: " + value);
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
