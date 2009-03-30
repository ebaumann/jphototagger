package de.elmar_baumann.imv.image.metadata.exif;

import java.util.Arrays;

/**
 * GPS altitude.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/17
 */
public final class ExifGpsAltitude {

    public enum Ref {

        OBOVE_SEA_LEVEL, BELOW_SEA_LEVEL
    }
    private Ref ref;
    private ExifRational value;

    public ExifGpsAltitude(Ref ref, byte[] rawValue, ExifRational.ByteOrder byteOrder) {
        this.ref = ref;
        if (rawValue.length != 8) throw new IllegalArgumentException("rawValue.length != 8");
        byte[] numerator = Arrays.copyOfRange(rawValue, 0, 4);
        byte[] denominator = Arrays.copyOfRange(rawValue, 4, 8);
        this.value = new ExifRational(numerator, denominator, byteOrder);
    }

    public Ref getRef() {
        return ref;
    }

    public ExifRational getValue() {
        return value;
    }
}
