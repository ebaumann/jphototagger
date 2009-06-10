package de.elmar_baumann.imv.image.metadata.exif;

import java.util.Arrays;

/**
 * A coordinate described in degrees (minutes and seconds are the 1/60 and
 * 1/3600 or a degree).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/30
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
     *         to {@link #getRawValueByteCount()}
     */
    public ExifDegrees(byte[] rawValue, ExifByteOrder byteOrder) {
        if (!isRawValueByteCountOk(rawValue))
            throw new IllegalArgumentException(
                    "Illegal raw value byte count: " + rawValue.length);
        degrees =
                new ExifRational(Arrays.copyOfRange(rawValue, 0, 8), byteOrder);
        minutes = new ExifRational(Arrays.copyOfRange(rawValue, 8, 16),
                byteOrder);
        seconds = new ExifRational(Arrays.copyOfRange(rawValue, 16, 24),
                byteOrder);
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
        return rawValue.length == getRawValueByteCount();
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
