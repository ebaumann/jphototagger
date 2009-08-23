package de.elmar_baumann.imv.cache;

import java.awt.Image;
import java.io.File;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class RenderedThumbnailCacheIndirection extends CacheIndirection {
    public Image thumbnail;
    public int length = 0;
    public boolean hasKeywords;  // actually contains keywords
    public boolean renderedForKeywords;  // was rendered for keywords

    public RenderedThumbnailCacheIndirection(File _file, int _length) {
        super(_file);
        thumbnail = null;
        length = _length;
    }

    @Override
    public boolean isEmpty() {
        return thumbnail == null;
    }
}
