package org.jphototagger.program.cache;

import java.awt.Image;

import java.io.File;

/**
 *
 * @author Martin Pohlack
 */
public class ThumbnailCacheIndirection extends CacheIndirection {
    Image thumbnail;

    public ThumbnailCacheIndirection(File _file) {
        super(_file);
        thumbnail = null;
    }

    @Override
    public boolean isEmpty() {
        return thumbnail == null;
    }
}
