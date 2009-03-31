package de.elmar_baumann.imv.image.metadata.exif;

import java.util.Arrays;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/30
 */
public final class ExifDegrees {

    private final ExifRational degrees;
    private final ExifRational minutes;
    private final ExifRational seconds;

    public ExifDegrees(byte[] rawValue, ExifMetadata.ByteOrder byteOrder) {
        if (rawValue.length != 24) throw new IllegalArgumentException("rawValue.length != 24");
        byte[] degreesNumerator = Arrays.copyOfRange(rawValue, 0, 4);
        byte[] degreesDenominator = Arrays.copyOfRange(rawValue, 4, 8);
        byte[] minutesNumerator = Arrays.copyOfRange(rawValue, 8, 12);
        byte[] minutesDenominator = Arrays.copyOfRange(rawValue, 12, 16);
        byte[] secondsNumerator = Arrays.copyOfRange(rawValue, 16, 20);
        byte[] secondsDenominator = Arrays.copyOfRange(rawValue, 20, 24);
        degrees = new ExifRational(degreesNumerator, degreesDenominator, byteOrder);
        minutes = new ExifRational(minutesNumerator, minutesDenominator, byteOrder);
        seconds = new ExifRational(secondsNumerator, secondsDenominator, byteOrder);
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
