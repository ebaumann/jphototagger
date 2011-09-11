package org.jphototagger.domain.repository;

import java.io.File;
import java.util.List;

import org.jphototagger.domain.imagecollections.ImageCollection;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ImageCollectionsRepository {

    boolean deleteImageCollection(String collectionName);

    int deleteImagesFromImageCollection(String collectionName, List<File> imageFiles);

    boolean existsImageCollection(String collectionName);

    List<String> getAllImageCollectionNames();

    List<ImageCollection> getAllImageCollections();

    int getImageCollectionCount();

    int getImageCountOfAllImageCollections();

    List<File> getImageFilesOfImageCollection(String collectionName);

    boolean insertImageCollection(String collectionName, List<File> imageFiles);

    boolean insertImageCollection(ImageCollection collection);

    boolean insertImagesIntoImageCollection(String collectionName, List<File> imageFiles);

    int updateRenameImageCollection(String fromName, String toName);
}
