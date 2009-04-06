package de.elmar_baumann.imv.image.metadata.exif;

import de.elmar_baumann.imv.image.metadata.exif.ExifMetadata.ByteOrder;
import java.nio.ByteBuffer;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/31
 */
public final class ExifUtil {

    public static double toDouble(ExifRational rational) {
        double numerator = rational.getNumerator();
        double denominator = rational.getDenominator();
        assert denominator > 0 : denominator;
        return numerator / denominator;
    }

    public static long toLong(ExifRational rational) {
        return (long) Math.floor(toDouble(rational));
    }

    public static int intFromRawValue(byte[] rawValue, ExifMetadata.ByteOrder byteOrder) {
        ByteBuffer buf = getByeBuffer(rawValue, byteOrder);
        return buf.getInt();
    }

    public static short shortFromRawValue(byte[] rawValue, ExifMetadata.ByteOrder byteOrder) {
        ByteBuffer buf = getByeBuffer(rawValue, byteOrder);
        return buf.getShort();
    }

    private static ByteBuffer getByeBuffer(byte[] rawValue, ByteOrder byteOrder) {
        ByteBuffer buf = ByteBuffer.wrap(rawValue);
        buf.order(byteOrder.equals(ExifMetadata.ByteOrder.LITTLE_ENDIAN)
                ? java.nio.ByteOrder.LITTLE_ENDIAN
                : java.nio.ByteOrder.BIG_ENDIAN);
        return buf;
    }

    private ExifUtil() {
    }
}
