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

import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.util.ByteUtil;
import java.nio.ByteOrder;
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
    private static final Map<String, Ref> REF_OF_STRING           = new HashMap<String, Ref>();
    private static final Map<Ref, String> LOCALIZED_STRING_OF_REF = new HashMap<Ref, String>();

    static {
        REF_OF_STRING.put("E", Ref.EAST);
        REF_OF_STRING.put("W", Ref.WEST);

        LOCALIZED_STRING_OF_REF.put(Ref.EAST, JptBundle.INSTANCE.getString("ExifGpsLongitudeRefEast"));
        LOCALIZED_STRING_OF_REF.put(Ref.WEST, JptBundle.INSTANCE.getString("ExifGpsLongitudeRefWest"));
    }
    private Ref         ref;
    private ExifDegrees degrees;

    public ExifGpsLongitude(byte[] refRawValue, byte[] degreesRawValue, ByteOrder byteOrder) {

        ensureByteCount(refRawValue, degreesRawValue);

        this.ref     = ref(refRawValue);
        this.degrees = new ExifDegrees(degreesRawValue, byteOrder);
    }

    private static Ref ref(byte[] rawValue) {
        String s = null;
        if (rawValue != null && rawValue.length == 2) {
            s = new StringBuilder(1).append((char) ByteUtil.toInt(rawValue[0])).toString();
        }
        return REF_OF_STRING.get(s);
    }

    public static int refByteCount() {
        return 2;
    }

    public static int byteCount() {
        return 24;
    }

    public static boolean byteCountOk(byte[] rawValue) {
        return rawValue.length == byteCount();
    }

    public static boolean refByteCountOk(byte[] rawValue) {
        return rawValue.length == refByteCount();
    }

    public String localizedString() {
        return ExifGpsUtil.degreesToString(degrees) + " " + LOCALIZED_STRING_OF_REF.get(ref);
    }

    public ExifDegrees degrees() {
        return degrees;
    }

    public Ref ref() {
        return ref;
    }

    private void ensureByteCount(byte[] refRawValue, byte[] degreesRawValue) throws IllegalArgumentException {

        if (!refByteCountOk(refRawValue))
            throw new IllegalArgumentException(
                    "Illegal ref raw value byte count: " + refRawValue.length);

        if (!byteCountOk(degreesRawValue))
            throw new IllegalArgumentException(
                    "Illegal raw value byte count: " + degreesRawValue.length);
    }
}
