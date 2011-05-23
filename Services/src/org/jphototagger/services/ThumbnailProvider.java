package org.jphototagger.services;

import java.awt.Image;
import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ThumbnailProvider {

    /**
     *
     * @param  imageFile
     * @return           thumbnail of the image file or null
     */
    Image getThumbnail(File imageFile);
}
