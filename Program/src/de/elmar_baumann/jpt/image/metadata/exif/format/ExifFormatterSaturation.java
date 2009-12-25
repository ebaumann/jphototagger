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
package de.elmar_baumann.jpt.image.metadata.exif.format;

import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import de.elmar_baumann.jpt.image.metadata.exif.IdfEntryProxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the type {@link ExifTag#SATURATION}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
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

    private ExifFormatterSaturation() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.SATURATION.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (exifKeyOfSaturation.containsKey(value)) {
                return TRANSLATION.translate(exifKeyOfSaturation.get(value));
            }
        }
        return "?";
    }
}
