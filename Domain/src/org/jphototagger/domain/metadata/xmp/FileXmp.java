package org.jphototagger.domain.metadata.xmp;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public final class FileXmp {

    private final File file;
    private final Xmp xmp;

    public FileXmp(File file, Xmp xmp) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        this.file = file;
        this.xmp = xmp;
    }

    public File getFile() {
        return file;
    }

    public Xmp getXmp() {
        return xmp;
    }
}
