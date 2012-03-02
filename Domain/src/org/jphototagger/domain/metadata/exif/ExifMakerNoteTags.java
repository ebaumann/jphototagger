package org.jphototagger.domain.metadata.exif;

import java.util.Collection;

/**
 * @author Elmar Baumann
 */
public interface ExifMakerNoteTags {

    /**
     *
     * @param make EXIF MAKE, tag id 271
     * @param model EXIF MODEL, tag id 272
     * @param makerNoteRawValue  raw value of maker note EXIF tag, tag id 37500
     * @return maker notes or empty collection
     */
    public Collection<ExifTag> getMakerNoteTags(String make, String model, byte[] makerNoteRawValue);
}
