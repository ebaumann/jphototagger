/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.image.metadata.exif.tag;

import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifDatatypeUtil;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifRational;
import de.elmar_baumann.jpt.resource.Bundle;
import java.nio.ByteOrder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * GPS altitude.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-03-17
 */
public final class ExifGpsAltitude {

    public enum Ref {

        OBOVE_SEA_LEVEL, BELOW_SEA_LEVEL
    }
    private static final Map<Integer, Ref> REF_OF_INTEGER          = new HashMap<Integer, Ref>();
    private static final Map<Ref, String>  LOCALIZED_STRING_OF_REF = new HashMap<Ref, String>();

    static {
        REF_OF_INTEGER.put(0, Ref.OBOVE_SEA_LEVEL);
        REF_OF_INTEGER.put(1, Ref.BELOW_SEA_LEVEL);

        LOCALIZED_STRING_OF_REF.put(Ref.OBOVE_SEA_LEVEL, Bundle.getString("ExifGpsAltitudeRefOboveSeaLevel"));
        LOCALIZED_STRING_OF_REF.put(Ref.BELOW_SEA_LEVEL, Bundle.getString("ExifGpsAltitudeRefBelowSeaLevel"));
    }
    private Ref ref;
    private ExifRational value;

    public ExifGpsAltitude(byte[] refRawValue, byte[] rawValue, ByteOrder byteOrder) {

        ensureByteCount(refRawValue, rawValue);

        this.ref   = ref(refRawValue);
        this.value = new ExifRational(Arrays.copyOfRange(rawValue, 0, 8), byteOrder);
    }

    /**
     * Returns the valid raw value reference byte count.
     *
     * @return valid raw value byte count
     */
    public static int refByteCount() {
        return 1;
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int byteCount() {
        return 8;
    }

    public static boolean byteCountOk(byte[] rawValue) {
        return rawValue.length == byteCount();
    }

    public static boolean refByteCountOk(byte[] rawValue) {
        return rawValue.length == refByteCount();
    }

    private static Ref ref(byte[] rawValue) {
        int i = new Byte(rawValue[0]).intValue();
        return REF_OF_INTEGER.get(i);
    }

    public String localizedString() {
        MessageFormat msg = new MessageFormat("{0} m {1}");
        return msg.format(new Object[]{ExifDatatypeUtil.toLong(value), LOCALIZED_STRING_OF_REF.get(ref)});
    }

    public Ref ref() {
        return ref;
    }

    public ExifRational value() {
        return value;
    }

    private void ensureByteCount(byte[] refRawValue, byte[] rawValue) throws IllegalArgumentException {

        if (!refByteCountOk(refRawValue))
            throw new IllegalArgumentException(
                    "Illegal ref raw value byte count: " + refRawValue.length);

        if (!byteCountOk(rawValue))
            throw new IllegalArgumentException(
                    "Illegal raw value byte count: " + rawValue.length);
    }
}
