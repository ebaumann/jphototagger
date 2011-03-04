package org.jphototagger.program.event.listener;

import java.io.File;

import java.util.List;

/**
 * Listens to events in
 * {@link org.jphototagger.program.database.DatabaseImageCollections}.
 *
 * @author Elmar Baumann
 */
public interface DatabaseImageCollectionsListener {

    /**
     * Will be called if an image collection was inserted into
     * {@link org.jphototagger.program.database.DatabaseImageCollections}.
     *
     * @param collectionName     name of the inserted collection
     * @param insertedImageFiles inserted image files
     */
    void collectionInserted(String collectionName, List<File> insertedImageFiles);

    /**
     * Will be called if an image collection was deleted from
     * {@link org.jphototagger.program.database.DatabaseImageCollections}.
     *
     * @param collectionName    name of the deleted collection
     * @param deletedImageFiles deleted image files
     */
    void collectionDeleted(String collectionName, List<File> deletedImageFiles);

    /**
     * Will be called if an image collection was renamed from
     * {@link org.jphototagger.program.database.DatabaseImageCollections}.
     *
     * @param fromName old name of the image collection
     * @param toName   new name of the image collection
     */
    void collectionRenamed(String fromName, String toName);

    /**
     * Will be called if images were inserted into
     * {@link org.jphototagger.program.database.DatabaseImageCollections}.
     *
     * @param collectionName     name of the image collection
     * @param insertedImageFiles inserted image files
     */
    void imagesInserted(String collectionName, List<File> insertedImageFiles);

    /**
     * Will be called if images were deleted from
     * {@link org.jphototagger.program.database.DatabaseImageCollections}.
     *
     * @param collectionName    name of the image collection
     * @param deletedImageFiles deleted image files
     */
    void imagesDeleted(String collectionName, List<File> deletedImageFiles);
}
