package org.jphototagger.program.image.metadata.exif.formatter.canon;

import org.jphototagger.program.image.metadata.exif.ExifTags;

import java.text.DecimalFormat;

//References:
//* http://www.burren.cx/david/canon.html
//* http://www.ozhiker.com/electronics/pjmt/jpeg_info/canon_mn.html
//* http://gvsoft.homedns.org/exif/canon_explain/Canon-PS-S50-111_1115-explain.html

/**
 *
 *
 * @author Elmar Baumann
 */
final class CanonMakerNoteTag4Formatter {
    private static final int CANON_TAG = 4;

    static void add(short[] values, ExifTags exifTags) {
        addSubjectDistance(values, exifTags);
    }

    private static void addSubjectDistance(short[] values, ExifTags exifTags) {
        final int offset = 19;
        final int valueIndex = offset - 1;

        if (valueIndex >= values.length) {
            return;    // No subject distance information available
        }

        short distance = values[valueIndex];

        if (distance <= 0) {
            return;
        }

        double factor = (distance / 1000 >= 1)
                        ? 1000
                        : 100;    // Don't know plausibility of that
        double distanceM = distance / factor;
        double distanceCm = distance / factor / 100;
        DecimalFormat dfCm = new DecimalFormat("#.## cm");
        DecimalFormat dfM = new DecimalFormat("#.# m");
        String d = (distanceM >= 1)
                   ? dfM.format(distanceM)
                   : dfCm.format(distanceCm);

        CanonMakerNotes.addTag(exifTags, CanonMakerNotes.tagId(CANON_TAG, offset), "SubjectDistance", d);
    }

    private CanonMakerNoteTag4Formatter() {}
}
