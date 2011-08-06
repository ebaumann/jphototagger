package org.jphototagger.domain.repository.event;

import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ThumbnailUpdatedEvent {

    private final Object source;
    private final File imageFile;

    public ThumbnailUpdatedEvent(Object source, File imageFile) {
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
