package org.jphototagger.domain.repository.event.imagecollections;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionDeletedEvent {

    private final Object source;
    private final String collectionName;
    private final List<File> deletedImageFiles;

    public ImageCollectionDeletedEvent(Object source, String collectionName, List<File> deletedImageFiles) {
        if (collectionName == null) {
            throw new NullPointerException("collectionName == null");
        }

        if (deletedImageFiles == null) {
            throw new NullPointerException("deletedImageFiles == null");
        }

        this.source = source;
        this.collectionName = collectionName;
        this.deletedImageFiles = new ArrayList<File>(deletedImageFiles);
    }

    public String getCollectionName() {
        return collectionName;
    }

    public List<File> getDeletedImageFiles() {
        return Collections.unmodifiableList(deletedImageFiles);
    }

    public Object getSource() {
        return source;
    }
}
