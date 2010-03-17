/*
 * @(#)CanonMakerNoteTag1Formatter.java    2010-01-13
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

package de.elmar_baumann.jpt.image.metadata.exif.formatter.canon;

import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTags;

import java.text.DecimalFormat;

//References:
//* http://www.burren.cx/david/canon.html
//* http://www.ozhiker.com/electronics/pjmt/jpeg_info/canon_mn.html
//* http://gvsoft.homedns.org/exif/canon_explain/Canon-PS-S50-111_1115-explain.html

/**
 *
 *
 * @author  Elmar Baumann
 */
final class CanonMakerNoteTag1Formatter {
    private static final int CANON_TAG = 1;

    static void add(short[] values, ExifTags exifTags) {
        addFocusMode(values, exifTags);
        addLens(values, exifTags);
    }

    private static void addLens(short[] values, ExifTags exifTags) {
        final int maxValueIndex = 24;

        if (maxValueIndex >= values.length) {
            return;    // No lens information available
        }

        double mmFactor = values[24];

        assert mmFactor > 0;

        if (mmFactor <= 0) {
            return;
        }

        double        minF           = (double) values[23] / mmFactor;
        double        maxF           = (double) values[22] / mmFactor;
        boolean       fixFocalLength = Math.abs(minF - maxF) < .0000001;
        DecimalFormat df             = new DecimalFormat("#.#");
        String        lens           = fixFocalLength
                                       ? df.format(minF) + " mm"
                                       : df.format(minF) + "-"
                                         + df.format(maxF) + " mm";

        CanonMakerNotes.addTag(exifTags, ExifTag.Id.MAKER_NOTE_LENS.value(),
                               "Lens", lens);
    }

    private static void addFocusMode(short[] values, ExifTags exifTags) {
        final int offset     = 8;
        final int valueIndex = offset - 1;

        if (valueIndex >= values.length) {
            return;    // No focus mode information available
        }

        short  value = values[valueIndex];
        String mode  = null;

        switch (value) {
        case 0 :
            mode = "One-Shot";

            break;

        case 1 :
            mode = "AI Servo";

            break;

        case 2 :
            mode = "AI Focus";

            break;

        case 3 :
            mode = "MF";

            break;

        case 4 :
            mode = "Single AF";

            break;

        case 5 :
            mode = "Continuous AF";

            break;

        case 6 :
            mode = "MF";

            break;

        default :
            return;
        }

        CanonMakerNotes.addTag(exifTags,
                               CanonMakerNotes.tagId(CANON_TAG, offset),
                               "FocusMode", mode);
    }

    private CanonMakerNoteTag1Formatter() {}
}
