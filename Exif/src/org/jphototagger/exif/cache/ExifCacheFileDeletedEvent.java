package org.jphototagger.exif.cache;

import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ExifCacheFileDeletedEvent {

    private final Object source;
    private final File imageFile;
    private final File cacheFile;

    public ExifCacheFileDeletedEvent(Object source, File imageFile, File cacheFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (cacheFile == null) {
            throw new NullPointerException("cacheFile == null");
        }

        this.source = source;
        this.imageFile = imageFile;
        this.cacheFile = cacheFile;
    }

    public File getCacheFile() {
        return cacheFile;
    }

    public File getImageFile() {
        return imageFile;
    }

    public Object getSource() {
        return source;
    }
}
