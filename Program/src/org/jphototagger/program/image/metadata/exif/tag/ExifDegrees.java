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
     * Creates a new instance.
     *
     * @param  rawValue   raw value
     * @param  byteOrder  byte order
     * @throws IllegalArgumentException if the length of raw value is not equals
     *         to {@link #byteCount()}
     */
    public ExifDegrees(byte[] rawValue, ByteOrder byteOrder) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }

        Ensure.length(rawValue, byteCount());
        degrees = new ExifRational(Arrays.copyOfRange(rawValue, 0, 8), byteOrder);
        minutes = new ExifRational(Arrays.copyOfRange(rawValue, 8, 16), byteOrder);
        seconds = new ExifRational(Arrays.copyOfRange(rawValue, 16, 24), byteOrder);
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int byteCount() {
        return 24;
    }

    public boolean byteCountOk(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        return rawValue.length == byteCount();
    }

    public ExifRational degrees() {
        return degrees;
    }

    public ExifRational minutes() {
        return minutes;
    }

    public ExifRational seconds() {
        return seconds;
    }
}
