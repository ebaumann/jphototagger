package org.jphototagger.exif.formatter.nikon;

/**
 *
 *
 * @author Elmar Baumann
 */
final class NikonMakerNoteTagIdExifTagId {

    private final int nikonMakerNoteTagId;
    private final int exifTagId;

    NikonMakerNoteTagIdExifTagId(int nikonTagId, int exifTagId) {
        this.nikonMakerNoteTagId = nikonTagId;
        this.exifTagId = exifTagId;
    }

    int getExifTagId() {
        return exifTagId;
    }

    int getNikonMakerNoteTagId() {
        return nikonMakerNoteTagId;
    }

}
