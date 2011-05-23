package org.jphototagger.program.image.metadata.exif.datatype;

/**
 * EXIF data type ASCII as described in the standard: An 8-bit byte containing
 * one 7-bit ASCII code. The final byte is terminated with NULL.
 *
 * @author Elmar Baumann
 */
public final class ExifAscii {
    
    private final String value;

    public ExifAscii(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        value = convertRawValueToString(rawValue);
    }

    public static String convertRawValueToString(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        String nullTerminatedValue = new String(rawValue);
        int length = nullTerminatedValue.length();

        return (length > 0)
               ? nullTerminatedValue.substring(0, length - 1)
               : "";
    }

    public static ExifDataType getExifDataType() {
        return ExifDataType.ASCII;
    }

    public String getValue() {
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

        if (!(obj instanceof ExifAscii)) {
            return false;
        }

        ExifAscii other = (ExifAscii) obj;

        return this.value == null
                                 ? other.value == null
                                 : this.value.equals(other.value);
        }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 37 * hash + this.value != null
                            ? this.value.hashCode()
                               : 0;

        return hash;
    }

    @Override
    public String toString() {
        return value;
    }
}
