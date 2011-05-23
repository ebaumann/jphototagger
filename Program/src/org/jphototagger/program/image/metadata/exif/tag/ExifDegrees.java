package org.jphototagger.program.image.metadata.exif.tag;

import org.jphototagger.program.image.metadata.exif.datatype.ExifRational;
import org.jphototagger.program.image.metadata.exif.Ensure;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * A coordinate described in degrees (minutes and seconds are the 1/60 and
 * 1/3600 or a degree).
 *
 * @author Elmar Baumann
 */
public final class ExifDegrees {

    private final ExifRational degrees;
    private final ExifRational minutes;
    private final ExifRational seconds;

    /**
     *
     * @param  rawValue
     * @param  byteOrder
     * @throws IllegalArgumentException if the length of raw value is not equals to {@link #getRawValueByteCount()}
     */
    public ExifDegrees(byte[] rawValue, ByteOrder byteOrder) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }

        Ensure.length(rawValue, getRawValueByteCount());
        degrees = new ExifRational(Arrays.copyOfRange(rawValue, 0, 8), byteOrder);
        minutes = new ExifRational(Arrays.copyOfRange(rawValue, 8, 16), byteOrder);
        seconds = new ExifRational(Arrays.copyOfRange(rawValue, 16, 24), byteOrder);
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int getRawValueByteCount() {
        return 24;
    }

    public boolean isRawValueByteCountOk(byte[] rawValue) {
        return rawValue == null
                ? false
                : rawValue.length == getRawValueByteCount();
        }

    public ExifRational getDegrees() {
        return degrees;
    }

    public ExifRational getMinutes() {
        return minutes;
    }

    public ExifRational getSeconds() {
        return seconds;
    }
}
