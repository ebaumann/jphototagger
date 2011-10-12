package org.jphototagger.domain.repository.event.imagecollections;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Elmar Baumann
 */
public final class ImageCollectionInsertedEvent {

    private final Object source;
    private final String collectionName;
    private final List<File> insertedImageFiles;

    public ImageCollectionInsertedEvent(Object source, String collectionName, List<File> insertedImageFiles) {
        if (collectionName == null) {
            throw new NullPointerException("collectionName == null");
        }

        if (insertedImageFiles == null) {
            throw new NullPointerException("insertedImageFiles == null");
        }

        this.source = source;
        this.collectionName = collectionName;
        this.insertedImageFiles = new ArrayList<File>(insertedImageFiles);
    }

    public String getCollectionName() {
        return collectionName;
    }

    public List<File> getInsertedImageFiles() {
        return Collections.unmodifiableList(insertedImageFiles);
    }

    public Object getSource() {
        return source;
    }
}
