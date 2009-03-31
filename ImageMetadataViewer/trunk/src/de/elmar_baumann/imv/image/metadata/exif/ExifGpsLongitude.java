package de.elmar_baumann.imv.image.metadata.exif;

import de.elmar_baumann.imv.resource.Bundle;
import java.util.HashMap;
import java.util.Map;

/**
 * GPS longitude.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/17
 */
public final class ExifGpsLongitude {

    public enum Ref {

        EAST, WEST
    }
    private static final Map<String, Ref> refOfString = new HashMap<String, Ref>();
    private static final Map<Ref, String> localizedStringOfRef = new HashMap<Ref, String>();


    static {
        refOfString.put("E", Ref.EAST);
        refOfString.put("W", Ref.WEST);

        localizedStringOfRef.put(Ref.EAST, Bundle.getString("ExifGpsLongitudeRefEast"));
        localizedStringOfRef.put(Ref.WEST, Bundle.getString("ExifGpsLongitudeRefWest"));
    }
    private Ref ref;
    private ExifDegrees degrees;

    public ExifGpsLongitude(byte[] refRawValue, byte[] degreesRawValue, ExifMetadata.ByteOrder byteOrder) {
        if (refRawValue.length != 2)
            throw new IllegalArgumentException("refRawValue.length != 2");
        if (degreesRawValue.length != 24)
            throw new IllegalArgumentException("rawValue.length != 24");

        this.ref = getRef(refRawValue);
        this.degrees = new ExifDegrees(degreesRawValue, byteOrder);
    }

    private static Ref getRef(byte[] rawValue) {
        String s = null;
        if (rawValue != null && rawValue.length == 2) {
            s = new StringBuilder(1).append((char) new Byte(rawValue[0]).intValue()).toString();
        }
        return refOfString.get(s);
    }

    private static String localizedString(Ref ref) {
        return localizedStringOfRef.get(ref);
    }

    public ExifDegrees getDegrees() {
        return degrees;
    }

    public Ref getRef() {
        return ref;
    }
}
