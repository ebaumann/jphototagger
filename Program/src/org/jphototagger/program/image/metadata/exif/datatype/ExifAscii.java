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

        value = decode(rawValue);
    }

    /**
     * Decodes a raw value.
     *
     * @param  rawValue raw value
     * @return          decoded value
     */
    public static String decode(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        String nullTerminatedValue = new String(rawValue);
        int    length              = nullTerminatedValue.length();

        return (length > 0)
               ? nullTerminatedValue.substring(0, length - 1)
               : "";
    }

    public static ExifDataType dataType() {
        return ExifDataType.ASCII;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ExifAscii other = (ExifAscii) obj;

        if ((this.value == null)
            ? (other.value != null)
            : !this.value.equals(other.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 37 * hash + ((this.value != null)
                            ? this.value.hashCode()
                            : 0);

        return hash;
    }

    @Override
    public String toString() {
        return value;
    }
}
