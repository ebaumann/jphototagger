package org.jphototagger.domain.repository.event.xmp;

import java.io.File;

import org.jphototagger.domain.metadata.xmp.Xmp;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class XmpUpdatedEvent {

    private final Object source;
    private final File imageFile;
    private final Xmp oldXmp;
    private final Xmp updatedXmp;

    public XmpUpdatedEvent(Object source, File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        this.source = source;
        this.imageFile = imageFile;
        this.oldXmp = oldXmp;
        this.updatedXmp = updatedXmp;
    }

    public File getImageFile() {
        return imageFile;
    }

    public Xmp getOldXmp() {
        return oldXmp;
    }

    public Object getSource() {
        return source;
    }

    public Xmp getUpdatedXmp() {
        return updatedXmp;
    }
}
