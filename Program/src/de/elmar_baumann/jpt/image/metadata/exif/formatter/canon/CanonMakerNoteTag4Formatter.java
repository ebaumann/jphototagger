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
 * @author  Elmar Baumann <eb@elmar-baumann.de>
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

        assert values.length >=  valueIndex;
        if (values.length < valueIndex) return;

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

        CanonMakerNotes.addTag(exifTags, CanonMakerNotes.tagId(CANON_TAG, offset), "Entfernung", d);
    }

    private CanonMakerNoteTag4Formatter() {
    }
}
