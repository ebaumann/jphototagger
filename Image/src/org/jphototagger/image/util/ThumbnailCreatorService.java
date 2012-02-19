package org.jphototagger.image.util;

import java.awt.Image;
import java.io.File;
import java.util.Collection;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.thumbnails.ThumbnailCreator;

/**
 * @author Elmar Baumann
 */
public final class ThumbnailCreatorService {

    public static final ThumbnailCreatorService INSTANCE = new ThumbnailCreatorService();
    private final Collection<? extends ThumbnailCreator> thumbnailCreators = Lookup.getDefault().lookupAll(ThumbnailCreator.class);

    /**
     * Queries all {@code ThumbnailCreator} implementations and returns
     * a thumbnail from the first implementation capable of creating it.
     *
     * @param file
     * @return thumbnail or null
     */
    public Image createThumbnail(File file) {

        for (ThumbnailCreator tnCreator : thumbnailCreators) {
            if (tnCreator.canCreateThumbnail(file)) {
                Image thumbnail = tnCreator.createThumbnail(file);

                if (thumbnail != null) {
                    return thumbnail;
                }
            }
        }

        return null;
    }

    /**
     * Queries all {@code ThumbnailCreator} implementations and returns
     * a thumbnail from the first implementation capable of creating it.
     *
     * @param file
     * @return thumbnail or null
     */
    public Image createFromEmbeddedThumbnail(File file) {

        for (ThumbnailCreator tnCreator : thumbnailCreators) {
            if (tnCreator.canCreateFromEmbeddedThumbnail(file)) {
                Image thumbnail = tnCreator.createFromEmbeddedThumbnail(file);

                if (thumbnail != null) {
                    return thumbnail;
                }
            }
        }

        return null;
    }

    /**
     * Calls {@code #createThumbnail(java.io.File)} and if not successful
     * {@code #createFromEmbeddedThumbnail(java.io.File)}.
     *
     * @param file
     * @return Thumbnail or null
     */
    public Image createScaledOrFromEmbeddedThumbnail(File file) {
        Image thumbnail = createThumbnail(file);

        if (thumbnail != null) {
            return thumbnail;
        }

        return createFromEmbeddedThumbnail(file);
    }

    public static int readMaxThumbnailWidthFromStorage() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        int width = prefs.getInt(Preferences.KEY_MAX_THUMBNAIL_WIDTH);

        return (width != Integer.MIN_VALUE)
                ? width
                : 150;
    }

    private ThumbnailCreatorService() {
    }
}
