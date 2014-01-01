package org.jphototagger.domain.thumbnails;

import java.awt.Image;
import java.io.File;

/**
 * @author Elmar Baumann
 */
public interface ThumbnailProvider {

    /**
     * Returns an usually already created thumbnail (in opposite to a
     * {@code ThumbnailCreator}, which creates a new/fresh thumbnail).
     *
     * @param imageFile
     * @return thumbnail of the image file or null
     */
    Image getThumbnail(File imageFile);
}
