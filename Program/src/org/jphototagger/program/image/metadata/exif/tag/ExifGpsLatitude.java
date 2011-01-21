package org.jphototagger.program.image.metadata.exif.tag;

import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.lib.util.ByteUtil;

import java.nio.ByteOrder;
import java.util.EnumMap;

import java.util.HashMap;
import java.util.Map;

/**
 * GPS latitude.
 *
 * @author Elmar Baumann
 */
public final class ExifGpsLatitude {

    /**
     * Indicates whether the latitude is north or south latitude.
     */
    public enum Ref { NORTH, SOUTH }

    private static final Map<String, Ref> REF_OF_STRING = new HashMap<String,
                                                              Ref>();
    private static final Map<Ref, String> LOCALIZED_STRING_OF_REF =
        new EnumMap<Ref, String>(Ref.class);

    static {
        REF_OF_STRING.put("N", Ref.NORTH);
        REF_OF_STRING.put("S", Ref.SOUTH);
        LOCALIZED_STRING_OF_REF.put(
            Ref.NORTH, JptBundle.INSTANCE.getString("ExifGpsLatitudeRefNorth"));
        LOCALIZED_STRING_OF_REF.put(
            Ref.SOUTH, JptBundle.INSTANCE.getString("ExifGpsLatitudeRefSouth"));
    }

    private Ref         ref;
    private ExifDegrees degrees;

    public ExifGpsLatitude(byte[] refRawValue, byte[] degreesRawValue,
                           ByteOrder byteOrder) {
        if (refRawValue == null) {
            throw new NullPointerException("refRawValue == null");
        }

        if (degreesRawValue == null) {
            throw new NullPointerException("degreesRawValue == null");
        }

        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }

        ensureByteCount(refRawValue, degreesRawValue);
        this.ref     = ref(refRawValue);
        this.degrees = new ExifDegrees(degreesRawValue, byteOrder);
    }

    private static Ref ref(byte[] rawValue) {
        String s = null;

        if ((rawValue != null) && (rawValue.length == 2)) {
            s = new StringBuilder(1).append(
                (char) ByteUtil.toInt(rawValue[0])).toString();
        }

        return REF_OF_STRING.get(s);
    }

    public static int byteCount() {
        return 24;
    }

    public static int refByteCount() {
        return 2;
    }

    public static boolean byteCountOk(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        return rawValue.length == byteCount();
    }

    public static boolean refByteCountOk(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        return rawValue.length == refByteCount();
    }

    public String localizedString() {
        return ExifGpsUtil.degreesToString(degrees) + " "
               + LOCALIZED_STRING_OF_REF.get(ref);
    }

    public ExifDegrees degrees() {
        return degrees;
    }

    public Ref ref() {
        return ref;
    }

    private void ensureByteCount(byte[] refRawValue, byte[] degreesRawValue)
            throws IllegalArgumentException {
        if (!refByteCountOk(refRawValue)) {
            throw new IllegalArgumentException(
                "Illegal ref raw value byte count: " + refRawValue.length);
        }

        if (!byteCountOk(degreesRawValue)) {
            throw new IllegalArgumentException("Illegal raw value byte count: "
                                               + degreesRawValue.length);
        }
    }
}
