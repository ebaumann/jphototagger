package org.jphototagger.program.cache;

import java.awt.Image;

/**
 * This object is responsible for rendering a complete thumbnail include
 * overlays, filename, border etc.
 *
 * It provides a single method and this method requires a single pre-scaled
 * thumbnail, all other sizes are based on the longer edge of the provided
 * image.
 *
 * @author Martin Pohlack
 */
public interface ThumbnailRenderer {
    Image getRenderedThumbnail(Image scaled,
                               RenderedThumbnailCacheIndirection rtci,
                               boolean dummy);
}
