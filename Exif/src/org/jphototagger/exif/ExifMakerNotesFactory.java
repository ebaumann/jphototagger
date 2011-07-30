package org.jphototagger.exif;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jphototagger.exif.formatter.canon.CanonMakerNotes;
import org.jphototagger.exif.formatter.nikon.NikonMakerNotes;

/**
 *
 * @author Elmar Baumann
 */
public final class ExifMakerNotesFactory {

    private static final Map<String, ExifMakerNotes> makerNotesOfMake = new HashMap<String, ExifMakerNotes>();

    static {
        makerNotesOfMake.put("nikon", new NikonMakerNotes());
        makerNotesOfMake.put("canon", new CanonMakerNotes());
    }

    static void add(File file, ExifTags exifTags) {
        ExifTag makerNoteTag = exifTags.findExifTagByTagId(ExifTag.Id.MAKER_NOTE.getTagId());
        ExifTag makeTag = exifTags.findExifTagByTagId(ExifTag.Id.MAKE.getTagId());
        String make = (makeTag == null)
                ? null
                : makeTag.getStringValue().toLowerCase();

        if ((makeTag != null) && (makerNoteTag != null)) {
            for (String mk : makerNotesOfMake.keySet()) {
                if (make.contains(mk)) {
                    makerNotesOfMake.get(mk).add(file, exifTags, makerNoteTag);
                }
            }
        }
    }

    private ExifMakerNotesFactory() {
    }
}
