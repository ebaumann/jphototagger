/*
 * @(#)ExifFormatterContrast.java    Created on 2009-06-10
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

package de.elmar_baumann.jpt.image.metadata.exif.formatter;

import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.jpt.image.metadata.exif.Ensure;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata.IfdType;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;

import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#CONTRAST}.
 *
 * @author  Elmar Baumann
 */
public final class ExifFormatterContrast extends ExifFormatter {
    public static final ExifFormatterContrast INSTANCE =
        new ExifFormatterContrast();
    private static final Map<Integer, String> EXIF_KEY_OF_CONTRAST =
        new HashMap<Integer, String>();

    static {
        EXIF_KEY_OF_CONTRAST.put(0, "ContrastNormal");
        EXIF_KEY_OF_CONTRAST.put(1, "ContrastLow");
        EXIF_KEY_OF_CONTRAST.put(2, "ContrastHigh");
    }

    @Override
    public String format(ExifTag exifTag) {
        Ensure.exifTagId(exifTag, ExifTag.Id.CONTRAST);

        if (ExifShort.byteCountOk(exifTag.rawValue())) {
            ExifShort es = new ExifShort(exifTag.rawValue(),
                                         exifTag.byteOrder());
            int value = es.value();

            if (EXIF_KEY_OF_CONTRAST.containsKey(value)) {
                return translate(IfdType.EXIF, EXIF_KEY_OF_CONTRAST.get(value));
            }
        }

        return "?";
    }

    private ExifFormatterContrast() {}
}
