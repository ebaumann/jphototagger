/*
 * @(#)ExifFormatterMeteringMode.java    Created on 2009-06-10
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifShort;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;

import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#METERING_MODE}.
 *
 * @author  Elmar Baumann
 */
public final class ExifFormatterMeteringMode extends ExifFormatter {
    public static final ExifFormatterMeteringMode INSTANCE =
        new ExifFormatterMeteringMode();
    private static final Map<Integer, String> EXIF_KEY_OF_METERING_MODE =
        new HashMap<Integer, String>();

    static {
        EXIF_KEY_OF_METERING_MODE.put(0, "MeteringModeUnknown");
        EXIF_KEY_OF_METERING_MODE.put(1, "MeteringModeIntegral");
        EXIF_KEY_OF_METERING_MODE.put(2, "MeteringModeIntegralCenter");
        EXIF_KEY_OF_METERING_MODE.put(3, "MeteringModeSpot");
        EXIF_KEY_OF_METERING_MODE.put(4, "MeteringModeMultiSpot");
        EXIF_KEY_OF_METERING_MODE.put(5, "MeteringModeMatrix");
        EXIF_KEY_OF_METERING_MODE.put(6, "MeteringModeSelective");
    }

    private ExifFormatterMeteringMode() {}

    @Override
    public String format(ExifTag exifTag) {
        Ensure.exifTagId(exifTag, ExifTag.Id.METERING_MODE);

        if (ExifShort.byteCountOk(exifTag.rawValue())) {
            ExifShort es = new ExifShort(exifTag.rawValue(),
                                         exifTag.byteOrder());
            int value = es.value();

            if (EXIF_KEY_OF_METERING_MODE.containsKey(value)) {
                return translate(IfdType.EXIF,
                                 EXIF_KEY_OF_METERING_MODE.get(value));
            }
        }

        return "?";
    }
}
