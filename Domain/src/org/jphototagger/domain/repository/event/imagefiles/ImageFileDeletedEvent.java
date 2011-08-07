package org.jphototagger.domain.repository.event.imagefiles;

import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageFileDeletedEvent {

    private final Object source;
    private final File imageFile;

    public ImageFileDeletedEvent(Object source, File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        this.source = source;
        this.imageFile = imageFile;
    }

    public Object getSource() {
        return source;
    }

    public File getImageFile() {
        return imageFile;
    }
}
