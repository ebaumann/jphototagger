package org.jphototagger.domain.metadata.exif;

/**
 * @author Elmar Baumann
 */
public interface ExifCacheProvider {

    void init();

    /**
     *
     * @return count of deleted information
     */
    int clear();
}
