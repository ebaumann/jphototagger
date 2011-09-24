package org.jphototagger.api.image.thumbnails;

import java.io.File;
import java.util.Collection;

/**
 *
 * @author Elmar Baumann
 */
public interface ThumbnailsDisplayer {

    void displayThumbnails(Collection<? extends File> files, OriginOfDisplayedThumbnails origin);
}
