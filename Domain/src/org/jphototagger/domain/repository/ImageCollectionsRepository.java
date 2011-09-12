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

    List<String> findAllImageCollectionNames();

    List<ImageCollection> findAllImageCollections();

    int getImageCollectionCount();

    int getImageCountOfAllImageCollections();

    List<File> findImageFilesOfImageCollection(String collectionName);

    boolean saveImageCollection(String collectionName, List<File> imageFiles);

    boolean saveImageCollection(ImageCollection collection);

    boolean insertImagesIntoImageCollection(String collectionName, List<File> imageFiles);

    int updateRenameImageCollection(String fromName, String toName);
}
