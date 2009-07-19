package de.elmar_baumann.imv.image.metadata.exif.datatype;

import java.nio.ByteBuffer;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-03-31
 */
public final class ExifDatatypeUtil {

    public static double toDouble(ExifRational rational) {
        double numerator = rational.getNumerator();
        double denominator = rational.getDenominator();
        assert denominator > 0 : denominator;
        return numerator / denominator;
    }

    public static long toLong(ExifRational rational) {
        return (long) Math.floor(toDouble(rational));
    }

    public static int intFromRawValue(byte[] rawValue, ExifByteOrder byteOrder) {
        ByteBuffer buf = getByeBuffer(rawValue, byteOrder);
        return buf.getInt();
    }

    public static short shortFromRawValue(byte[] rawValue,
            ExifByteOrder byteOrder) {
        ByteBuffer buf = getByeBuffer(rawValue, byteOrder);
        return buf.getShort();
    }

    private static ByteBuffer getByeBuffer(byte[] rawValue,
            ExifByteOrder byteOrder) {
        ByteBuffer buf = ByteBuffer.wrap(rawValue);
        buf.order(byteOrder.equals(ExifByteOrder.LITTLE_ENDIAN)
                  ? java.nio.ByteOrder.LITTLE_ENDIAN
                  : java.nio.ByteOrder.BIG_ENDIAN);
        return buf;
    }

    private ExifDatatypeUtil() {
    }
}
