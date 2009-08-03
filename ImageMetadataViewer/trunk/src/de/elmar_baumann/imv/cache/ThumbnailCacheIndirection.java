package de.elmar_baumann.imv.cache;

import java.awt.Image;
import java.io.File;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class ThumbnailCacheIndirection extends CacheIndirection {
    public Image thumbnail;
    public Image scaled;

    public ThumbnailCacheIndirection(File _file) {
        super(_file);
        thumbnail = null;
        scaled = null;
    }

    @Override
    public boolean isEmpty() {
        return thumbnail == null;
    }
}