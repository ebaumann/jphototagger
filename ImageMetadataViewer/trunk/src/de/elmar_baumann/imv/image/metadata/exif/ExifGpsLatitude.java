package de.elmar_baumann.imv.image.metadata.exif;

import de.elmar_baumann.imv.resource.Bundle;
import java.util.HashMap;
import java.util.Map;

/**
 * GPS latitude.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/17
 */
public final class ExifGpsLatitude {

    /**
     * Indicates whether the latitude is north or south latitude.
     */
    public enum Ref {

        NORTH, SOUTH
    }
    private static final Map<String, Ref> refOfString = new HashMap<String, Ref>();
    private static final Map<Ref, String> localizedStringOfRef = new HashMap<Ref, String>();


    static {
        refOfString.put("N", Ref.NORTH);
        refOfString.put("S", Ref.SOUTH);

        localizedStringOfRef.put(Ref.NORTH, Bundle.getString("ExifGpsLatitudeRefNorth"));
        localizedStringOfRef.put(Ref.SOUTH, Bundle.getString("ExifGpsLatitudeRefSouth"));
    }
    private Ref ref;
    private ExifDegrees degrees;

    public ExifGpsLatitude(Ref ref, byte[] degreesRawValue, ExifRational.ByteOrder byteOrder) {
        this.ref = ref;
        this.degrees = new ExifDegrees(degreesRawValue, byteOrder);
    }

    /**
     * Returns whether the latitude is north or south latitude.
     *
     * @param  rawValue  two ASCII values as defined in the EXIF 2.2 standard
     * @return Reference or null if the string is not valid
     */
    public static Ref getRef(byte[] rawValue) {
        String s = null;
        if (rawValue != null && rawValue.length == 2) {
            s = new StringBuilder(1).append((char) new Byte(rawValue[0]).intValue()).toString();
        }
        return refOfString.get(s);
    }

    /**
     * Returns a localized string of the latitude indicator.
     *
     * @param  ref latitude indicator
     * @return localized string
     */
    public static String localizedString(Ref ref) {
        return localizedStringOfRef.get(ref);
    }

    public ExifDegrees getDegrees() {
        return degrees;
    }
}
