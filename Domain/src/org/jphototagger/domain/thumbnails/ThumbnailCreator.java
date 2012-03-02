package org.jphototagger.domain.thumbnails;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.util.Set;

import org.jphototagger.api.collections.PositionProvider;

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
public interface ThumbnailCreator extends PositionProvider {

    /**
     *
     * @param file create thumbnail from this file
     * @return thumbnail or null
     */
    Image createFromEmbeddedThumbnail(File file);

    /**
     *
     * @param file create thumbnail from this file
     * @return true if the creator can create
     */
    boolean canCreateFromEmbeddedThumbnail(File file);

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
     * @return true if the creator can create
     */
    boolean canCreateThumbnail(File file);

    /**
     * Suffixes for both embedded or scaled thumbnails.
     *
     * @return suffixes without leading dot, e.g. "jpg", "cr2", "nef"
     */
    Set<String> getAllSupportedFileTypeSuffixes();

    Set<String> getSupportedRawFormatFileTypeSuffixes();

    /**
     * @return maybe null
     */
    Component getSettingsComponent();

    String getDisplayName();
}
