/*
 * @(#)ExifFormatterSaturation.java    Created on 2009-06-10
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
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#SATURATION}.
 *
 * @author  Elmar Baumann
 */
public final class ExifFormatterSaturation extends ExifFormatter {
    public static final ExifFormatterSaturation INSTANCE =
        new ExifFormatterSaturation();
    private static final Map<Integer, String> exifKeyOfSaturation =
        new HashMap<Integer, String>();

    static {
        exifKeyOfSaturation.put(0, "SaturationNormal");
        exifKeyOfSaturation.put(1, "SaturationLow");
        exifKeyOfSaturation.put(2, "SaturationHigh");
    }

    private ExifFormatterSaturation() {}

    @Override
    public String format(ExifTag exifTag) {
        Ensure.exifTagId(exifTag, ExifTag.Id.SATURATION);

        if (ExifShort.byteCountOk(exifTag.rawValue())) {
            ExifShort es = new ExifShort(exifTag.rawValue(),
                                         exifTag.byteOrder());
            int value = es.value();

            if (exifKeyOfSaturation.containsKey(value)) {
                return translate(IfdType.EXIF, exifKeyOfSaturation.get(value));
            }
        }

        return "?";
    }
}
