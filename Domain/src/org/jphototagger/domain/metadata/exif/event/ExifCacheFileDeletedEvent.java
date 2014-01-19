package org.jphototagger.domain.metadata.exif.event;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public final class ExifCacheFileDeletedEvent {

    private final Object source;
    private final File imageFile;

    public ExifCacheFileDeletedEvent(Object source, File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        this.source = source;
        this.imageFile = imageFile;
    }

    public File getImageFile() {
        return imageFile;
    }

    public Object getSource() {
        return source;
    }
}
