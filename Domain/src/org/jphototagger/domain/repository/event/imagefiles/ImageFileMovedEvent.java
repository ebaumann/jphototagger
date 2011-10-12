package org.jphototagger.domain.repository.event.imagefiles;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public final class ImageFileMovedEvent {

    private final Object source;
    private final File oldImageFile;
    private final File newImageFile;

    public ImageFileMovedEvent(Object source, File oldImageFile, File newImageFile) {
        if (oldImageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (newImageFile == null) {
            throw new NullPointerException("newImageFile == null");
        }

        this.source = source;
        this.oldImageFile = oldImageFile;
        this.newImageFile = newImageFile;
    }

    public Object getSource() {
        return source;
    }

    public File getOldImageFile() {
        return oldImageFile;
    }

    public File getNewImageFile() {
        return newImageFile;
    }
}
