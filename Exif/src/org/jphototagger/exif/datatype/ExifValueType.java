package org.jphototagger.exif.datatype;

/**
 * Value type of an EXIF tag.
 *
 * @author Elmar Baumann
 */
public enum ExifValueType {

    /**
     * An 8-bit unsigned integer.
     * <p>
     * <ul>
     * <li>Value: 1</li>
     * <li>Bit Count: 8</li>
     * </ul>
     */
    BYTE(1, 8),
    /**
     * An 8-bit byte containing one 7-bit ASCII code. The final byte is terminated with NULL.
     * <p>
     * <ul>
     * <li>Value: 2</li>
     * <li>Bit Count: 8</li>
     * </ul>
     */
    ASCII(2, 8),
    /**
     * A 16-bit (2-byte) unsigned integer.
     * <p>
     * <ul>
     * <li>Value: 3</li>
     * <li>Bit Count: 16</li>
     * </ul>
     */
    SHORT(3, 16),
    /**
     * A 32-bit (4-byte) unsigned integer,
     * <ul>
     * <li>Value: 4</li>
     * <li>Bit Count: 32</li>
     * </ul>
     */
    LONG(4, 32),
    /**
     * Two LONGs. The first LONG is the numerator and the second LONG expresses the denominator.
     * <ul>
     * <li>Value: 5</li>
     * <li>Bit Count: 64</li>
     * </ul>
     */
    RATIONAL(5, 64),
    /**
     * An 8-bit byte that can take any value depending on the field definition.
     * <ul>
     * <li>Value: 7</li>
     * <li>Bit Count: 8</li>
     * </ul>
     */
    UNDEFINED(7, 8),
    /**
     * A 32-bit (4-byte) signed integer (2's complement notation).
     * <ul>
     * <li>Value: 9</li>
     * <li>Bit Count: 32</li>
     * </ul>
     */
    SLONG(9, 32),
    /**
     * Two SLONGs. The first SLONG is the numerator and the second SLONG is the denominator.
     * <ul>
     * <li>Value: 10</li>
     * <li>Bit Count: 64</li>
     * </ul>
     */
    SRATIONAL(10, 64),
    /**
     * Ambigious type.
     * <ul>
     * <li>Value: -1</li>
     * <li>Bit Count: -1</li>
     * </ul>
     */
    SHORT_OR_LONG(-1, -1),;

    private final int intValue;
    private final int bitCount;

    private ExifValueType(int intValue, int bitCount) {
        this.intValue = intValue;
        this.bitCount = bitCount;
    }

    /**
     * Returns the value as defined in the EXIF standard (Bytes 2 + 3 in the IFD identifing the type).
     *
     * @return value
     */
    public int getIntValue() {
        return intValue;
    }

    /**
     * Returns the bit count of the data type.
     *
     * @return bit count
     */
    public int getBitCount() {
        return bitCount;
    }

    /**
     * @param anInt
     * @return value type or {@link #UNDEFINED} if the int is not convertible
     *        (no element with {@link #getIntValue()} == anInt exists)
     */
    public static ExifValueType parseInt(int anInt) {
        for (ExifValueType valueType : values()) {
            if (valueType.intValue == anInt) {
                return valueType;
            }
        }
        return UNDEFINED;
    }

    public static boolean isIntValue(int anInt) {
        for (ExifValueType valueType : values()) {
            if (valueType.intValue == anInt) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return intValue + " (" + name() + ")";
    }
}
