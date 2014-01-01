package org.jphototagger.program.module.thumbnails.cache;

import java.io.Serializable;

/**
 * @author Elmar Baumann
 */
public final class Thumbnail implements Serializable {

    private static final long serialVersionUID = 1L;
    private final long lastModified;
    private final byte[] thumbnailBytes;

    public Thumbnail(byte[] thumbnailBytes, long imageTimeStamp) {
        this.thumbnailBytes = thumbnailBytes;
        this.lastModified = imageTimeStamp;
    }

    public byte[] getThumbnailBytes() {
        return thumbnailBytes;
    }

    public long getLastModified() {
        return lastModified;
    }
}
