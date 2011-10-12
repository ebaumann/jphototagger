package org.jphototagger.domain.thumbnails.event;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;

/**
 * @author Elmar Baumann
 */
public final class ThumbnailsChangedEvent {

    private final Object source;
    private final OriginOfDisplayedThumbnails originOfDisplayedThumbnails;
    private final List<File> imageFiles;

    public ThumbnailsChangedEvent(Object source, OriginOfDisplayedThumbnails originOfDisplayedThumbnails, List<File> imageFiles) {
        if (originOfDisplayedThumbnails == null) {
            throw new NullPointerException("typeOfDisplayedImages == null");
        }

        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        this.source = source;
        this.originOfDisplayedThumbnails = originOfDisplayedThumbnails;
        this.imageFiles = imageFiles;
    }

    public List<File> getImageFiles() {
        return Collections.unmodifiableList(imageFiles);
    }

    public Object getSource() {
        return source;
    }

    public boolean isEmpty() {
        return imageFiles.isEmpty();
    }

    public int getThumbnailCount() {
        return imageFiles.size();
    }

    public OriginOfDisplayedThumbnails getOriginOfDisplayedThumbnails() {
        return originOfDisplayedThumbnails;
    }
}
