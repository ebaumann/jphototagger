package de.elmar_baumann.imv.image.metadata.exif;

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

    public ExifByte(byte[] rawValue) {
        if (rawValue.length != 1)
            throw new IllegalArgumentException("raw Value length != 1: " + rawValue.length);
        value = (int) rawValue[0];
        if (value < 0)
            throw new IllegalArgumentException("value < 0: " + value);
    }

    public int getValue() {
        return value;
    }
}
