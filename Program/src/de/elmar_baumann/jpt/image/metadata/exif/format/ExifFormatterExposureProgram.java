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
 * Formats an EXIF entry of the type {@link ExifTag#EXPOSURE_PROGRAM}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterExposureProgram extends ExifFormatter {

    public static final ExifFormatterExposureProgram INSTANCE =
            new ExifFormatterExposureProgram();
    private static final Map<Integer, String> EXIF_KEY_OF_EXPOSURE_PROGRAM =
            new HashMap<Integer, String>();

    static {
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(0, "ExposureProgramUnkonwn"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(1, "ExposureProgramManual"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(2, "ExposureProgramNormalProgram"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(3, "ExposureProgramAperturePriority"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(4, "ExposureProgramTimePriority"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(5, "ExposureProgramCreativ"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(6, "ExposureProgramAction"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(7, "ExposureProgramPortrait"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(8, "ExposureProgramLandscape"); // NOI18N
    }

    private ExifFormatterExposureProgram() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.EXPOSURE_PROGRAM.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (EXIF_KEY_OF_EXPOSURE_PROGRAM.containsKey(value)) {
                return TRANSLATION.translate(EXIF_KEY_OF_EXPOSURE_PROGRAM.get(
                        value));
            }
        }
        return "?"; // NOI18N
    }
}
