package org.jphototagger.domain.xmp;

import java.io.File;
import org.jphototagger.domain.xmp.Xmp;

/**
 *
 *
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
