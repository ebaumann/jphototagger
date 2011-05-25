package org.jphototagger.services.core;

import java.io.File;
import java.util.Collection;

/**
 * Displays thumbnails whithin JPhotoTagger's program window (main area).
 *
 * @author Elmar Baumann
 */
public interface ThumbnailsDisplayer {

    void displayThumbnailsOfImageFiles(Collection<? extends File> imageFiles);
}
