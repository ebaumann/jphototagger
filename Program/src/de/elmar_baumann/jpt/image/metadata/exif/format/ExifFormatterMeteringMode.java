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
 * Formats an EXIF entry of the type {@link ExifTag#METERING_MODE}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterMeteringMode extends ExifFormatter {

    public static final ExifFormatterMeteringMode INSTANCE =
            new ExifFormatterMeteringMode();
    private static final Map<Integer, String> EXIF_KEY_OF_METERING_MODE =
            new HashMap<Integer, String>();

    static {
        EXIF_KEY_OF_METERING_MODE.put(0, "MeteringModeUnknown"); // NOI18N
        EXIF_KEY_OF_METERING_MODE.put(1, "MeteringModeIntegral"); // NOI18N
        EXIF_KEY_OF_METERING_MODE.put(2, "MeteringModeIntegralCenter"); // NOI18N
        EXIF_KEY_OF_METERING_MODE.put(3, "MeteringModeSpot"); // NOI18N
        EXIF_KEY_OF_METERING_MODE.put(4, "MeteringModeMultiSpot"); // NOI18N
        EXIF_KEY_OF_METERING_MODE.put(5, "MeteringModeMatrix"); // NOI18N
        EXIF_KEY_OF_METERING_MODE.put(6, "MeteringModeSelective"); // NOI18N
    }

    private ExifFormatterMeteringMode() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.METERING_MODE.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (EXIF_KEY_OF_METERING_MODE.containsKey(value)) {
                return TRANSLATION.translate(
                        EXIF_KEY_OF_METERING_MODE.get(value));
            }
        }
        return "?"; // NOI18N
    }
}
