package org.jphototagger.exif.formatter.canon;

import java.io.File;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.exif.ExifIfdType;
import org.jphototagger.exif.ExifMakerNotes;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.ExifTags;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class CanonMakerNotes implements ExifMakerNotes {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("org/jphototagger/exif/formatter/canon/Bundle");

    @Override
    public void add(File file, ExifTags exifTags, ExifTag makerNoteTag) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (exifTags == null) {
            throw new NullPointerException("exifTags == null");
        }

        if (makerNoteTag == null) {
            throw new NullPointerException("makerNoteTag == null");
        }

        CanonIfd ifd = new CanonIfd(makerNoteTag.getRawValue(), makerNoteTag.convertByteOrderIdToByteOrder());
        short[] tag1Values = CanonMakerNote.getTag1Values(file, ifd);
        short[] tag4Values = CanonMakerNote.getTag4Values(file, ifd);

        if (tag1Values != null) {
            CanonMakerNoteTag1Formatter.add(tag1Values, exifTags);
        }

        if (tag4Values != null) {
            CanonMakerNoteTag4Formatter.add(tag4Values, exifTags);
        }
    }

    static int tagId(int canonTag, int offset) {
        return ExifTag.Id.MAKER_NOTE_CANON_START.getTagId() + canonTag * 100 + offset;
    }

    static void addTag(ExifTags exifTags, int tagId, String nameBundleKey, String value) {
        try {
            exifTags.addMakerNoteTag(new ExifTag(tagId, -1, -1, -1, null, value, -1, BUNDLE.getString(nameBundleKey), ExifIfdType.MAKER_NOTE));
        } catch (Exception ex) {
            Logger.getLogger(CanonMakerNotes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
