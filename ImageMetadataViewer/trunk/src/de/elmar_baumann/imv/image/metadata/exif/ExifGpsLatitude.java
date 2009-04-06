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

    public ExifGpsLatitude(byte[] refRawValue, byte[] degreesRawValue, ExifMetadata.ByteOrder byteOrder) {
        if (!isRefRawValueByteCountOk(refRawValue))
            throw new IllegalArgumentException("Illegal ref raw value byte count: " + refRawValue.length);
        if (!isRawValueByteCountOk(degreesRawValue))
            throw new IllegalArgumentException("Illegal raw value byte count: " + degreesRawValue.length);

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

    public static int getRawValueByteCount() {
        return 24;
    }

    public static int getRefRawValueByteCount() {
        return 2;
    }

    public static boolean isRawValueByteCountOk(byte[] rawValue) {
        return rawValue.length == getRawValueByteCount();
    }

    public static boolean isRefRawValueByteCountOk(byte[] rawValue) {
        return rawValue.length == getRefRawValueByteCount();
    }

    public String localizedString() {
        return ExifGpsUtil.degreesToString(degrees) + " " + localizedStringOfRef.get(ref);
    }

    public ExifDegrees getDegrees() {
        return degrees;
    }

    public Ref getRef() {
        return ref;
    }
}
