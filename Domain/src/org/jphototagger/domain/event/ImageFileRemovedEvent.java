package org.jphototagger.domain.event;

import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageFileRemovedEvent {

    private final Object source;
    private final File imageFile;

    public ImageFileRemovedEvent(Object source, File imageFile) {
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
