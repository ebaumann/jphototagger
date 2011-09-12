package org.jphototagger.api.image;

import java.awt.Image;
import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ThumbnailCreator {

    Image createFromEmbeddedThumbnail(File file);

    Image createThumbnail(File file);
}
