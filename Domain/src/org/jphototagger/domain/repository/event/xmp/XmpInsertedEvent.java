package org.jphototagger.domain.repository.event.xmp;

import java.io.File;

import org.jphototagger.domain.metadata.xmp.Xmp;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class XmpInsertedEvent {

    private final Object source;
    private final File imageFile;
    private final Xmp xmp;

    public XmpInsertedEvent(Object source, File imageFile, Xmp xmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        this.source = source;
        this.imageFile = imageFile;
        this.xmp = xmp;
    }

    public File getImageFile() {
        return imageFile;
    }

    public Object getSource() {
        return source;
    }

    public Xmp getXmp() {
        return xmp;
    }
}
