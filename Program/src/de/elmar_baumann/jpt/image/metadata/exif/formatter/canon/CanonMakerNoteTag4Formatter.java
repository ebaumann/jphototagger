/*
 * JPhotoTagger tags and finds images fast.
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

import de.elmar_baumann.jpt.image.metadata.exif.ExifTags;
import java.text.DecimalFormat;

// References:
// * http://www.burren.cx/david/canon.html
// * http://www.ozhiker.com/electronics/pjmt/jpeg_info/canon_mn.html
// * http://gvsoft.homedns.org/exif/canon_explain/Canon-PS-S50-111_1115-explain.html

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-01-13
 */
final class CanonMakerNoteTag4Formatter {

    private static final int CANON_TAG = 4;

    static void add(short[] values, ExifTags exifTags) {
        addSubjectDistance(values, exifTags);
    }

    private static void addSubjectDistance(short[] values, ExifTags exifTags) {
        final int offset = 19;
        final int valueIndex = offset - 1;

        if (valueIndex >= values.length) return; // No subject distance information available

        short distance   = values[valueIndex];
        if (distance <= 0) return;

        double        factor     = distance / 1000 >= 1 ? 1000 : 100; // Don't know plausibility of that
        double        distanceM  = distance / factor;
        double        distanceCm = distance / factor / 100;
        DecimalFormat dfCm       = new DecimalFormat("#.## cm");
        DecimalFormat dfM        = new DecimalFormat("#.# m");

        String d = distanceM >= 1
                ? dfM.format(distanceM)
                : dfCm.format(distanceCm)
                ;

        CanonMakerNotes.addTag(exifTags, CanonMakerNotes.tagId(CANON_TAG, offset), "SubjectDistance", d);
    }

    private CanonMakerNoteTag4Formatter() {
    }
}
