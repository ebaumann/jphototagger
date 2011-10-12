package org.jphototagger.domain.thumbnails;

import java.io.File;
import java.util.Collection;

/**
 * @author Elmar Baumann
 */
public interface ThumbnailsDisplayer {

    void displayThumbnails(Collection<? extends File> files, OriginOfDisplayedThumbnails origin);
}
