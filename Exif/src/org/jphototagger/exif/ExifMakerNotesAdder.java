package org.jphototagger.exif;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.jphototagger.exif.formatter.canon.CanonMakerNotes;
import org.jphototagger.exif.formatter.nikon.NikonMakerNotes;

/**
 * @author Elmar Baumann
 */
public final class ExifMakerNotesAdder {

    private static final Map<String, ExifMakerNotes> MAKER_NOTES_OF_MAKE = new HashMap<String, ExifMakerNotes>();

    static {
        MAKER_NOTES_OF_MAKE.put("nikon", new NikonMakerNotes());
        MAKER_NOTES_OF_MAKE.put("canon", new CanonMakerNotes());
    }

    static void addMakerNotesToExifTags(File file, ExifTags exifTags) {
        ExifTag makerNoteTag = exifTags.findExifTagByTagId(ExifTag.Id.MAKER_NOTE.getTagId());
        ExifTag makeTag = exifTags.findExifTagByTagId(ExifTag.Id.MAKE.getTagId());
        String makeLowerCase = (makeTag == null)
                ? null
                : makeTag.getStringValue().toLowerCase();

        if (makeTag != null && makerNoteTag != null) {
            for (String makeSubstringLowercase : MAKER_NOTES_OF_MAKE.keySet()) {
                if (makeLowerCase.contains(makeSubstringLowercase)) {
                    ExifMakerNotes makerNotes = MAKER_NOTES_OF_MAKE.get(makeSubstringLowercase);

                    makerNotes.add(file, exifTags, makerNoteTag);
                }
            }
        }
    }

    private ExifMakerNotesAdder() {
    }
}
