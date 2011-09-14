package org.jphototagger.api.image;

import java.awt.Image;
import java.io.File;
import java.util.Set;

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

    /**
     *
     * @param file create thumbnail from this file
     * @return thumbnail or null
     */
    Image createFromEmbeddedThumbnail(File file);

    /**
     *
     * @param file create thumbnail from this file
     * @return
     */
    boolean canCreateEmbeddedThumbnail(File file);

    /**
     * Scales down a (huge) image.
     *
     * @param file create thumbnail from this file
     * @return thumbnail or null
     */
    Image createThumbnail(File file);

    /**
     *
     * @param file create thumbnail from this file
     * @return
     */
    boolean canCreateThumbnail(File file);

    /**
     * Suffixes for both embedded or scaled thumbnails.
     *
     * @return suffixes without leading dot, e.g. "cr2", "nef"
     */
    Set<String> getAllSupportedFileTypeSuffixes();

    Set<String> getSupportedRawFormatFileTypeSuffixes();
}
