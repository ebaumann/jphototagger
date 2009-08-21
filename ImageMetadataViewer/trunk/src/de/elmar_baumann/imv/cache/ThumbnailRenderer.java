package de.elmar_baumann.imv.cache;

import java.awt.Image;
import java.io.File;

/**
 * This object is responsible for rendering a complete thumbnail include
 * overlays, filename, border etc.
 *
 * It provides a single method and this method requires a single pre-scaled
 * thumbnail, all other sizes are based on the longer edge of the provided
 * image.
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-08-17
 */
public interface ThumbnailRenderer {
    public Image getRenderedThumbnail(Image scaled, File file, boolean dummy);
}
