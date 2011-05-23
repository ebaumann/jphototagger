package org.jphototagger.program.image.metadata.exif.tag;

import org.jphototagger.lib.util.ByteUtil;
import org.jphototagger.program.image.metadata.exif.datatype.ExifDatatypeUtil;
import org.jphototagger.program.image.metadata.exif.datatype.ExifRational;
import org.jphototagger.program.resource.JptBundle;
import java.nio.ByteOrder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * GPS altitude.
 *
 * @author Elmar Baumann
 */
public final class ExifGpsAltitude {
    public enum Ref { OBOVE_SEA_LEVEL, BELOW_SEA_LEVEL }

    private static final Map<Integer, Ref> REF_OF_INTEGER = new HashMap<Integer, Ref>();
    private static final Map<Ref, String> LOCALIZED_STRING_OF_REF = new EnumMap<Ref, String>(Ref.class);

    static {
        REF_OF_INTEGER.put(0, Ref.OBOVE_SEA_LEVEL);
        REF_OF_INTEGER.put(1, Ref.BELOW_SEA_LEVEL);
        LOCALIZED_STRING_OF_REF.put(Ref.OBOVE_SEA_LEVEL, JptBundle.INSTANCE.getString("ExifGpsAltitudeRefOboveSeaLevel"));
        LOCALIZED_STRING_OF_REF.put(Ref.BELOW_SEA_LEVEL, JptBundle.INSTANCE.getString("ExifGpsAltitudeRefBelowSeaLevel"));
    }

    private Ref ref;
    private ExifRational value;

    public ExifGpsAltitude(byte[] refRawValue, byte[] rawValue, ByteOrder byteOrder) {
        if (refRawValue == null) {
            throw new NullPointerException("refRawValue == null");
        }

        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }

        ensureByteCount(refRawValue, rawValue);
        this.ref = convertRawValueToRef(refRawValue);
        this.value = new ExifRational(Arrays.copyOfRange(rawValue, 0, 8), byteOrder);
    }

    /**
     * Returns the valid raw getValue reference byte count.
     *
     * @return valid raw getValue byte count
     */
    public static int getRefByteCount() {
        return 1;
    }

    /**
     * Returns the valid raw getValue byte count.
     *
     * @return valid raw getValue byte count
     */
    public static int getRawValueByteCount() {
        return 8;
    }

    public static boolean isRawValueByteCountOk(byte[] rawValue) {
        return rawValue == null
                ? false
                : rawValue.length == getRawValueByteCount();
        }

    public static boolean isRefByteCountOk(byte[] rawValue) {
        return rawValue == null
                ? false
                : rawValue.length == getRefByteCount();
    }

    private static Ref convertRawValueToRef(byte[] rawValue) {
        return REF_OF_INTEGER.get(ByteUtil.toInt(rawValue[0]));
    }

    public String getLocalizedString() {
        MessageFormat msg = new MessageFormat("{0} m {1}");

        return msg.format(new Object[] { ExifDatatypeUtil.convertExifRationalToLong(value), LOCALIZED_STRING_OF_REF.get(ref) });
    }

    public Ref getRef() {
        return ref;
    }

    public ExifRational getValue() {
        return value;
    }

    private void ensureByteCount(byte[] refRawValue, byte[] rawValue) throws IllegalArgumentException {
        if (!isRefByteCountOk(refRawValue)) {
            throw new IllegalArgumentException("Illegal ref raw value byte count: " + refRawValue.length);
        }

        if (!isRawValueByteCountOk(rawValue)) {
            throw new IllegalArgumentException("Illegal raw value byte count: " + rawValue.length);
        }
    }
}
