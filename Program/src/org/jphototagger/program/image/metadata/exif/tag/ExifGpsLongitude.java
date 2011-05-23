package org.jphototagger.program.image.metadata.exif.tag;

import org.jphototagger.lib.util.ByteUtil;
import org.jphototagger.program.resource.JptBundle;
import java.nio.ByteOrder;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * GPS longitude.
 *
 * @author Elmar Baumann
 */
public final class ExifGpsLongitude {

    public enum Ref {
        EAST,
        WEST;

        public boolean isWest() {
            return this.equals(WEST);
        }

        public boolean isEast() {
            return this.equals(EAST);
        }
    }

    private static final Map<String, Ref> REF_OF_STRING = new HashMap<String, Ref>();
    private static final Map<Ref, String> LOCALIZED_STRING_OF_REF = new EnumMap<Ref, String>(Ref.class);

    static {
        REF_OF_STRING.put("E", Ref.EAST);
        REF_OF_STRING.put("W", Ref.WEST);
        LOCALIZED_STRING_OF_REF.put(Ref.EAST, JptBundle.INSTANCE.getString("ExifGpsLongitudeRefEast"));
        LOCALIZED_STRING_OF_REF.put(Ref.WEST, JptBundle.INSTANCE.getString("ExifGpsLongitudeRefWest"));
    }

    private Ref ref;
    private ExifDegrees degrees;

    public ExifGpsLongitude(byte[] refRawValue, byte[] degreesRawValue, ByteOrder byteOrder) {
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
        this.ref = convertRawValueToRef(refRawValue);
        this.degrees = new ExifDegrees(degreesRawValue, byteOrder);
    }

    private static Ref convertRawValueToRef(byte[] rawValue) {
        String s = null;

        if ((rawValue != null) && (rawValue.length == 2)) {
            s = new StringBuilder(1).append((char) ByteUtil.toInt(rawValue[0])).toString();
        }

        return REF_OF_STRING.get(s);
    }

    public static int getRefByteCount() {
        return 2;
    }

    public static int getRawValueByteCount() {
        return 24;
    }

    public static boolean isRawValueByteCountOk(byte[] rawValue) {
        return rawValue == null
                ? false
                : rawValue.length == getRawValueByteCount();
        }

    public static boolean refByteCountOk(byte[] rawValue) {
        return rawValue == null
                ? false
                : rawValue.length == getRefByteCount();
        }

    public String toLocalizedString() {
        return ExifGpsUtil.getDegreesAsString(degrees) + " " + LOCALIZED_STRING_OF_REF.get(ref);
    }

    public ExifDegrees getExifDegrees() {
        return degrees;
    }

    public Ref getRef() {
        return ref;
    }

    private void ensureByteCount(byte[] refRawValue, byte[] degreesRawValue) throws IllegalArgumentException {
        if (!refByteCountOk(refRawValue)) {
            throw new IllegalArgumentException("Illegal ref raw value byte count: " + refRawValue.length);
        }

        if (!isRawValueByteCountOk(degreesRawValue)) {
            throw new IllegalArgumentException("Illegal raw value byte count: " + degreesRawValue.length);
        }
    }
}
