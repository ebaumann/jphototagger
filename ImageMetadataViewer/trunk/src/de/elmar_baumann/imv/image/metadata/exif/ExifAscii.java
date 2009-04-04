package de.elmar_baumann.imv.image.metadata.exif;

/**
 * EXIF data type ASCII as described in the standard: An 8-bit byte containing
 * one 7-bit ASCII code. The final byte is terminated with NULL.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/04/04
 */
public final class ExifAscii {

    private final String value;

    /**
     * Creates a new instance.
     *
     * @param rawValue  raw value
     */
    public ExifAscii(byte[] rawValue) {
        String nullTerminatedValue = new String(rawValue);
        int length = nullTerminatedValue.length();
        value = length > 0 ? nullTerminatedValue.substring(0, length - 1) : "";
    }

    /**
     * Returns the value.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }
}
