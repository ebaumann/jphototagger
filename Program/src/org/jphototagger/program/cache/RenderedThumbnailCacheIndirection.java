package org.jphototagger.program.cache;

import java.awt.Image;

import java.io.File;

/**
 *
 * @author Martin Pohlack
 */
public class RenderedThumbnailCacheIndirection extends CacheIndirection {
    public Image thumbnail;
    public int length = 0;
    public boolean hasKeywords;    // actually contains keywords
    public boolean renderedForKeywords;    // was rendered for keywords

    public RenderedThumbnailCacheIndirection(File _file, int _length) {
        super(_file);
        thumbnail = null;

        if (length < 0) {
            throw new IllegalArgumentException("Invalid length: " + length);
        }

        length = _length;
    }

    @Override
    public boolean isEmpty() {
        return thumbnail == null;
    }
}
