package org.jphototagger.program.image.metadata.exif.datatype;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ExifDatatypeUtil {
    public static double toDouble(ExifRational rational) {
        if (rational == null) {
            throw new NullPointerException("rational == null");
        }

        double numerator   = rational.numerator();
        double denominator = rational.denominator();

        assert denominator > 0 : denominator;

        return numerator / denominator;
    }

    public static long toLong(ExifRational rational) {
        if (rational == null) {
            throw new NullPointerException("rational == null");
        }

        return (long) Math.floor(toDouble(rational));
    }

    public static int intFromRawValue(byte[] rawValue, ByteOrder byteOrder) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }

        ByteBuffer buf = getByeBuffer(rawValue, byteOrder);

        return buf.getInt();
    }

    public static short shortFromRawValue(byte[] rawValue,
            ByteOrder byteOrder) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }

        ByteBuffer buf = getByeBuffer(rawValue, byteOrder);

        return buf.getShort();
    }

    private static ByteBuffer getByeBuffer(byte[] rawValue,
            ByteOrder byteOrder) {
        ByteBuffer buf = ByteBuffer.wrap(rawValue);

        buf.order(byteOrder);

        return buf;
    }

    private ExifDatatypeUtil() {}
}
