package org.jphototagger.domain.repository.event.exif;

import java.io.File;

import org.jphototagger.domain.exif.Exif;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ExifUpdatedEvent {

    private final Object source;
    private final File imageFile;
    private final Exif oldExif;
    private final Exif updatedExif;

    public ExifUpdatedEvent(Object source, File imageFile, Exif oldExif, Exif updatedExif) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (oldExif == null) {
            throw new NullPointerException("oldExif == null");
        }

        this.source = source;
        this.imageFile = imageFile;
        this.oldExif = oldExif;
        this.updatedExif = updatedExif;
    }

    public File getImageFile() {
        return imageFile;
    }

    public Exif getOldExif() {
        return oldExif;
    }

    public Object getSource() {
        return source;
    }

    public Exif getUpdatedExif() {
        return updatedExif;
    }
}
