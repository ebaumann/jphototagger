package org.jphototagger.api.image;

import java.awt.Image;
import java.io.File;

/**
 * Creates a thumbnail, can extend JPhotoTagger's capability
 * generating thumbnails for various file formats.
 * <p>
 * Hint getting the preferred maximum width from the user settings:
 * <pre>
 * Storage storage = Lookup.getDefault().lookup(Storage.class);
 * int width = storage.getInt(Storage.KEY_MAX_THUMBNAIL_WIDTH);
 * </pre>
 *
 * @author Elmar Baumann
 */
public interface ThumbnailCreator {

    Image createFromEmbeddedThumbnail(File file);

    Image createThumbnail(File file);
}
