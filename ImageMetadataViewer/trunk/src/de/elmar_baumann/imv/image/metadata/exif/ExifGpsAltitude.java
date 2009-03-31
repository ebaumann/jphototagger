package de.elmar_baumann.imv.image.metadata.exif;

import de.elmar_baumann.imv.resource.Bundle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    private static final Map<String, Ref> refOfString = new HashMap<String, Ref>();
    private static final Map<Ref, String> localizedStringOfRef = new HashMap<Ref, String>();


    static {
        refOfString.put("0", Ref.OBOVE_SEA_LEVEL);
        refOfString.put("1", Ref.BELOW_SEA_LEVEL);

        localizedStringOfRef.put(Ref.OBOVE_SEA_LEVEL, Bundle.getString("ExifGpsAltitudeRefOboveSeaLevel"));
        localizedStringOfRef.put(Ref.BELOW_SEA_LEVEL, Bundle.getString("ExifGpsAltitudeRefBelowSeaLevel"));
    }
    private Ref ref;
    private ExifRational value;

    public ExifGpsAltitude(byte[] refRawValue, byte[] rawValue, ExifMetadata.ByteOrder byteOrder) {

        if (refRawValue.length != 1)
            throw new IllegalArgumentException("refRawValue.length != 2");
        if (rawValue.length != 8)
            throw new IllegalArgumentException("rawValue.length != 8");

        byte[] numerator = Arrays.copyOfRange(rawValue, 0, 4);
        byte[] denominator = Arrays.copyOfRange(rawValue, 4, 8);

        this.ref = getRef(rawValue);
        this.value = new ExifRational(numerator, denominator, byteOrder);
    }

    private static Ref getRef(byte[] rawValue) {
        String s = null;
        s = new StringBuilder(1).append((char) new Byte(rawValue[0]).intValue()).toString();
        return refOfString.get(s);
    }

    private static String localizedString(Ref ref) {
        return localizedStringOfRef.get(ref);
    }

    public Ref getRef() {
        return ref;
    }

    public ExifRational getValue() {
        return value;
    }
}
