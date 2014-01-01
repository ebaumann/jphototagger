package org.jphototagger.xmp;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public final class ImageFileSidecarFile {

    private final File imageFile;
    private final File sidecarFile;

    public ImageFileSidecarFile(File imageFile, File sidecarFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        this.imageFile = imageFile;
        this.sidecarFile = sidecarFile;
    }

    public File getImageFile() {
        return imageFile;
    }

    public File getSidecarFile() {
        return sidecarFile;
    }
}
