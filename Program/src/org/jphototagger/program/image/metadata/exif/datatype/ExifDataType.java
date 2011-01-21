package org.jphototagger.program.image.metadata.exif.datatype;

/**
 * Data type of an EXIF tag.
 *
 * @author Elmar Baumann
 */
public enum ExifDataType {

    /**
     * An 8-bit unsigned integer.
     * <p>
     * <ul>
     * <li>Value: 1</li>
     * <li>Bit Count: 8</li>
     * </ul>
     */
    BYTE(1, 8, "BYTE"),

    /**
     * An 8-bit byte containing one 7-bit ASCII code. The final byte is terminated with NULL.
     * <p>
     * <ul>
     * <li>Value: 2</li>
     * <li>Bit Count: 8</li>
     * </ul>
     */
    ASCII(2, 8, "ASCII"),

    /**
     * A 16-bit (2-byte) unsigned integer.
     * <p>
     * <ul>
     * <li>Value: 3</li>
     * <li>Bit Count: 16</li>
     * </ul>
     */
    SHORT(3, 16, "SHORT"),

    /**
     * A 32-bit (4-byte) unsigned integer,
     * <ul>
     * <li>Value: 4</li>
     * <li>Bit Count: 32</li>
     * </ul>
     */
    LONG(4, 32, "LONG"),

    /**
     * Two LONGs. The first LONG is the numerator and the second LONG expresses
     * thedenominator.
     * <ul>
     * <li>Value: 5</li>
     * <li>Bit Count: 64</li>
     * </ul>
     */
    RATIONAL(5, 64, "RATIONAL"),

    /**
     * An 8-bit byte that can take any value depending on the field definition.
     * <ul>
     * <li>Value: 7</li>
     * <li>Bit Count: 8</li>
     * </ul>
     */
    UNDEFINED(7, 8, "UNDEFINED"),

    /**
     * A 32-bit (4-byte) signed integer (2's complement notation).
     * <ul>
     * <li>Value: 9</li>
     * <li>Bit Count: 32</li>
     * </ul>
     */
    SLONG(9, 32, "SLONG"),

    /**
     * Two SLONGs. The first SLONG is the numerator and the second SLONG is the
     * denominator.
     * <ul>
     * <li>Value: 10</li>
     * <li>Bit Count: 64</li>
     * </ul>
     */
    SRATIONAL(10, 64, "SRATIONAL"),

    /**
     * JPhotoTagger internal.
     * <ul>
     * <li>Value: -1</li>
     * <li>Bit Count: -1</li>
     * </ul>
     */
    SHORT_OR_LONG(-1, -1, "SHORT_OR_LONG"),
    ;

    private final int    value;
    private final int    bitCount;
    private final String string;

    private ExifDataType(int value, int bitCount, String string) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }
        this.value    = value;
        this.bitCount = bitCount;
        this.string   = string;
    }

    /**
     * Returns the value as defined in the EXIF standard (Bytes 2 + 3 in the
     * IFD identifing the type).
     *
     * @return value
     */
    public int value() {
        return value;
    }

    /**
     * Returns the bit count of the data type.
     *
     * @return bit count
     */
    public int bitCount() {
        return bitCount;
    }

    public static ExifDataType fromType(int type) {
        for (ExifDataType dataType : values()) {
            if (dataType.value == type) {
                return dataType;
            }
        }

        assert false : type;

        return UNDEFINED;
    }

    public static boolean isType(int type) {
        for (ExifDataType dataType : values()) {
            if (dataType.value == type) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the string representation of the Value.
     *
     * @return string
     */
    public String valueString() {
        return string;
    }

    @Override
    public String toString() {
        return value + " (" + string + ")";
    }
}
