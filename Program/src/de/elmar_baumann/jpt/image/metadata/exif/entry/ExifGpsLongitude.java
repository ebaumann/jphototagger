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
package de.elmar_baumann.jpt.image.metadata.exif.entry;

import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifByteOrder;
import de.elmar_baumann.jpt.resource.Bundle;
import java.util.HashMap;
import java.util.Map;

/**
 * GPS longitude.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-03-17
 */
public final class ExifGpsLongitude {

    public enum Ref {

        EAST, WEST
    }
    private static final Map<String, Ref> REF_OF_STRING =
            new HashMap<String, Ref>();
    private static final Map<Ref, String> LOCALIZED_STRING_OF_REF =
            new HashMap<Ref, String>();

    static {
        REF_OF_STRING.put("E", Ref.EAST);
        REF_OF_STRING.put("W", Ref.WEST);

        LOCALIZED_STRING_OF_REF.put(Ref.EAST, Bundle.getString("ExifGpsLongitudeRefEast"));
        LOCALIZED_STRING_OF_REF.put(Ref.WEST, Bundle.getString("ExifGpsLongitudeRefWest"));
    }
    private Ref ref;
    private ExifDegrees degrees;

    public ExifGpsLongitude(byte[] refRawValue, byte[] degreesRawValue,
            ExifByteOrder byteOrder) {
        if (!isRefRawValueByteCountOk(refRawValue))
            throw new IllegalArgumentException(
                    "Illegal ref raw value byte count: " + refRawValue.length);
        if (!isRawValueByteCountOk(degreesRawValue))
            throw new IllegalArgumentException(
                    "Illegal raw value byte count: " + degreesRawValue.length);

        this.ref = getRef(refRawValue);
        this.degrees = new ExifDegrees(degreesRawValue, byteOrder);
    }

    private static Ref getRef(byte[] rawValue) {
        String s = null;
        if (rawValue != null && rawValue.length == 2) {
            s = new StringBuilder(1).append((char) new Byte(rawValue[0]).
                    intValue()).toString();
        }
        return REF_OF_STRING.get(s);
    }

    public static int getRefRawValueByteCount() {
        return 2;
    }

    public static int getRawValueByteCount() {
        return 24;
    }

    public static boolean isRawValueByteCountOk(byte[] rawValue) {
        return rawValue.length == getRawValueByteCount();
    }

    public static boolean isRefRawValueByteCountOk(byte[] rawValue) {
        return rawValue.length == getRefRawValueByteCount();
    }

    public String localizedString() {
        return ExifGpsUtil.degreesToString(degrees) + " " +
                LOCALIZED_STRING_OF_REF.get(ref);
    }

    public ExifDegrees getDegrees() {
        return degrees;
    }

    public Ref getRef() {
        return ref;
    }
}
