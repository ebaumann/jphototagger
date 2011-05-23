package org.jphototagger.program.image.metadata.exif;

import org.jphototagger.program.image.metadata.exif.formatter.canon.CanonMakerNotes;
import org.jphototagger.program.image.metadata.exif.formatter.nikon.NikonMakerNotes;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

    private ExifMakerNotesFactory() {}
}
