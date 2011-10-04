package org.jphototagger.exif.cache;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ExifCacheClearedEvent {

    private final Object source;
    private final int deletedCacheFileCount;

    public ExifCacheClearedEvent(Object source, int deletedCacheFileCount) {
        if (deletedCacheFileCount < 0) {
            throw new IllegalArgumentException("Negative deletion count: " + deletedCacheFileCount);
        }
        this.source = source;
        this.deletedCacheFileCount = deletedCacheFileCount;
    }

    public Object getSource() {
        return source;
    }

    public int getDeletedCacheFileCount() {
        return deletedCacheFileCount;
    }
}
