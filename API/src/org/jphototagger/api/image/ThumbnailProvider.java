package org.jphototagger.api.image;

import java.awt.Image;
import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ThumbnailProvider {

    public static final int MIN_THUMBNAIL_WIDTH = 50;
    public static final int MAX_THUMBNAIL_WIDTH = 400;
    public static final int DEFAULT_THUMBNAIL_WIDTH = 150;

    /**
     * Returns an usually already created thumbnail (in opposite to a
     * {@link ThumbnailCreator}, which creates a new/fresh thumbnail).
     *
     * @param  imageFile
     * @return           thumbnail of the image file or null
     */
    Image getThumbnail(File imageFile);
}
