package org.jphototagger.exif.datatype;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @author Elmar Baumann
 */
public final class ExifValueUtil {

    public static double convertExifRationalToDouble(ExifRational rational) {
        if (rational == null) {
            throw new NullPointerException("rational == null");
        }
        double numerator = rational.getNumerator();
        double denominator = rational.getDenominator();
        assert denominator > 0 : denominator;
        return numerator / denominator;
    }

    public static long convertExifRationalToLong(ExifRational rational) {
        if (rational == null) {
            throw new NullPointerException("rational == null");
        }
        return (long) Math.floor(convertExifRationalToDouble(rational));
    }

    public static int convertRawValueToInt(byte[] rawValue, ByteOrder byteOrder) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }
        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }
        ByteBuffer buf = getByteBuffer(rawValue, byteOrder);
        return buf.getInt();
    }

    public static short convertRawValueToShort(byte[] rawValue, ByteOrder byteOrder) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }
        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }
        ByteBuffer buf = getByteBuffer(rawValue, byteOrder);
        return buf.getShort();
    }

    private static ByteBuffer getByteBuffer(byte[] rawValue, ByteOrder byteOrder) {
        ByteBuffer buf = ByteBuffer.wrap(rawValue);
        buf.order(byteOrder);
        return buf;
    }

    /**
     * @param value
     * @param byteOrder
     * @param byteCount
     * @return raw value of a long
     * @throws IllegalArgumentException if byte order is neither little endian nor big endian or if the value
     *         does not fit into {@code byteCount}
     */
    public static byte[] createRawValue(Long value, ByteOrder byteOrder, int byteCount) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }
        if (!ByteOrder.BIG_ENDIAN.equals(byteOrder) && !ByteOrder.LITTLE_ENDIAN.equals(byteOrder)) {
            throw new IllegalArgumentException("Neither big nor little endian byte order: " + byteOrder);
        }
        if (byteCount < 1) {
            throw new IllegalArgumentException("Byte count < 1: " + byteCount);
        }
        int requiredBytes = (Long.numberOfTrailingZeros(Long.highestOneBit(value)) + 8) / 8;
        if (byteCount < requiredBytes) {
            throw new IllegalArgumentException(requiredBytes + " byte(s) required, " + value + " does not fit into " + byteCount + " byte(s)");
        }
        byte[] byteValue = ByteBuffer.allocate(8).order(byteOrder).putLong(value).array();
        byte[] rawValue = new byte[byteCount];
        Arrays.fill(rawValue, (byte) 0);
        int maxRead = byteCount > 8 ? 8 : byteCount;
        if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
            System.arraycopy(byteValue, 8 - maxRead, rawValue, byteCount - maxRead, maxRead);
        } else {
            System.arraycopy(byteValue, 0, rawValue, 0, maxRead);
        }
        return rawValue;
    }

    private ExifValueUtil() {
    }
}
