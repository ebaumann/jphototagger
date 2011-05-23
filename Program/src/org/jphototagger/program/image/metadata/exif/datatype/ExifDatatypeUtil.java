package org.jphototagger.program.image.metadata.exif.datatype;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ExifDatatypeUtil {
    
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

    private ExifDatatypeUtil() {}
}
