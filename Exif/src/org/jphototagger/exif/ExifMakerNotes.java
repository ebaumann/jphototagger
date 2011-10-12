package org.jphototagger.exif;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public interface ExifMakerNotes {

    void add(File file, ExifTags exifTags, ExifTag makerNoteTag);
}
