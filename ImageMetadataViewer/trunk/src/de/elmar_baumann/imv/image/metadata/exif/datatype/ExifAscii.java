package de.elmar_baumann.imv.image.metadata.exif.datatype;

/**
 * EXIF data type ASCII as described in the standard: An 8-bit byte containing
 * one 7-bit ASCII code. The final byte is terminated with NULL.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/04/04
 */
public final class ExifAscii {

    /**
     * Decodes a raw value.
     *
     * @param  rawValue raw value
     * @return          decoded value
     */
    public static String decode(byte[] rawValue) {
        String nullTerminatedValue = new String(rawValue);
        int length = nullTerminatedValue.length();
        return length > 0 ? nullTerminatedValue.substring(0, length - 1) : ""; // NOI18N
    }

    public ExifType getDataTyp() {
        return ExifType.ASCII;
    }

    private ExifAscii() {
    }
}
