package de.elmar_baumann.imv.image.metadata.exif;

import de.elmar_baumann.imv.resource.Bundle;
import java.text.MessageFormat;
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
    private static final Map<Integer, Ref> refOfInteger = new HashMap<Integer, Ref>();
    private static final Map<Ref, String> localizedStringOfRef = new HashMap<Ref, String>();


    static {
        refOfInteger.put(0, Ref.OBOVE_SEA_LEVEL);
        refOfInteger.put(1, Ref.BELOW_SEA_LEVEL);

        localizedStringOfRef.put(Ref.OBOVE_SEA_LEVEL, Bundle.getString("ExifGpsAltitudeRefOboveSeaLevel"));
        localizedStringOfRef.put(Ref.BELOW_SEA_LEVEL, Bundle.getString("ExifGpsAltitudeRefBelowSeaLevel"));
    }
    private Ref ref;
    private ExifRational value;

    public ExifGpsAltitude(byte[] refRawValue, byte[] rawValue, ExifMetadata.ByteOrder byteOrder) {

        if (!isRefRawValueByteCountOk(refRawValue))
            throw new IllegalArgumentException("Illegal ref raw value byte count: " + refRawValue.length);
        if (!isRawValueByteCountOk(rawValue))
            throw new IllegalArgumentException("Illegal raw value byte count: " + rawValue.length);

        this.ref = getRef(refRawValue);
        this.value = new ExifRational(Arrays.copyOfRange(rawValue, 0, 8), byteOrder);
    }

    /**
     * Returns the valid raw value reference byte count.
     *
     * @return valid raw value byte count
     */
    public static int getRefRawValueByteCount() {
        return 1;
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int getRawValueByteCount() {
        return 8;
    }

    public static boolean isRawValueByteCountOk(byte[] rawValue) {
        return rawValue.length == getRawValueByteCount();
    }

    public static boolean isRefRawValueByteCountOk(byte[] rawValue) {
        return rawValue.length == getRefRawValueByteCount();
    }

    private static Ref getRef(byte[] rawValue) {
        int i = new Byte(rawValue[0]).intValue();
        return refOfInteger.get(i);
    }

    public String localizedString() {
        MessageFormat msg = new MessageFormat("{0} m {1}");
        Object[] params = {ExifUtil.toLong(value), localizedStringOfRef.get(ref)};
        return msg.format(params);
    }

    public Ref getRef() {
        return ref;
    }

    public ExifRational getValue() {
        return value;
    }
}
