package de.elmar_baumann.jpt.image.metadata.exif;

import java.io.File;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-13
 */
public interface ExifMakerNotes {

    public void add(File file, ExifTags exifTags, ExifTag makerNoteTag);
}
