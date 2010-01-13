package de.elmar_baumann.jpt.image.metadata.exif.formatter.canon;

import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
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
final class CanonMakerNoteTag1Formatter {

    private static final int CANON_TAG = 1;

    static void add(short[] values, ExifTags exifTags) {
        addFocusMode(values, exifTags);
        addLens     (values, exifTags);
    }

    private static void addLens(short[] values, ExifTags exifTags) {
        assert values.length >= 24;
        if (values.length < 24) return;

        double mmFactor = values[24];
        assert mmFactor > 0;
        if (mmFactor <= 0) return;

        double    minF           = (double) values[23] / mmFactor;
        double    maxF           = (double) values[22] / mmFactor;
        boolean   fixFocalLength = minF == maxF;
        DecimalFormat df         = new DecimalFormat("#.#");

        String lens = fixFocalLength
                ? df.format(minF) + " mm"
                : df.format(minF) + "-" + df.format(maxF) + " mm"
                ;

        CanonMakerNotes.addTag(exifTags, ExifTag.Id.MAKER_NOTE_LENS.value(), "Objektiv", lens);
    }

    private static void addFocusMode(short[] values, ExifTags exifTags) {
        final int offset     = 8;
        final int valueIndex = offset - 1;

        assert values.length >= valueIndex;
        if (values.length < valueIndex) return;

        short value = values[valueIndex];
        String mode = null;
        switch (value) {
            case 0: mode = "One-Shot"       ; break;
            case 1: mode = "AI Servo"       ; break;
            case 2: mode = "AI Focus"       ; break;
            case 3: mode = "MF"             ; break;
            case 4: mode = "Single AF"      ; break;
            case 5: mode = "Continuous AF"  ; break;
            case 6: mode = "MF"             ; break;
            default: return;
        }
        CanonMakerNotes.addTag(exifTags, CanonMakerNotes.tagId(CANON_TAG, offset), "Scharfeinstellung", mode);
    }

    private CanonMakerNoteTag1Formatter() {
    }
}
