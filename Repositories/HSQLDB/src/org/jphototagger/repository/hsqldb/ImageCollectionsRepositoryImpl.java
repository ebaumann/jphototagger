package org.jphototagger.repository.hsqldb;

import java.io.File;
import java.util.List;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ImageCollectionsRepository.class)
public final class ImageCollectionsRepositoryImpl implements ImageCollectionsRepository {

    @Override
    public boolean deleteImageCollection(String collectionName) {
        return ImageCollectionsDatabase.INSTANCE.deleteImageCollection(collectionName, false);
    }

    @Override
    public int deleteImagesFromImageCollection(String collectionName, List<File> imageFiles) {
        return ImageCollectionsDatabase.INSTANCE.deleteImagesFromImageCollection(collectionName, imageFiles);
    }

    @Override
    public boolean existsImageCollection(String collectionName) {
        return ImageCollectionsDatabase.INSTANCE.existsImageCollection(collectionName);
    }

    @Override
    public List<String> findAllImageCollectionNames() {
        return ImageCollectionsDatabase.INSTANCE.getAllImageCollectionNames();
    }

    @Override
    public List<ImageCollection> findAllImageCollections() {
        return ImageCollectionsDatabase.INSTANCE.getAllImageCollections();
    }

    @Override
    public int getImageCollectionCount() {
        return ImageCollectionsDatabase.INSTANCE.getImageCollectionCount();
    }

    @Override
    public int getImageCountOfAllImageCollections() {
        return ImageCollectionsDatabase.INSTANCE.getImageCountOfAllImageCollections();
    }

    @Override
    public List<File> findImageFilesOfImageCollection(String collectionName) {
        return ImageCollectionsDatabase.INSTANCE.getImageFilesOfImageCollection(collectionName);
    }

    @Override
    public boolean containsFile(String collectionName, String filename) {
        return ImageCollectionsDatabase.INSTANCE.containsFile(collectionName, filename);
    }

    @Override
    public boolean saveImageCollection(String collectionName, List<File> imageFiles) {
        return ImageCollectionsDatabase.INSTANCE.insertImageCollection(collectionName, imageFiles);
    }

    @Override
    public boolean saveImageCollection(ImageCollection collection) {
        return ImageCollectionsDatabase.INSTANCE.insertImageCollection(collection);
    }

    @Override
    public boolean insertImagesIntoImageCollection(String collectionName, List<File> imageFiles) {
        return ImageCollectionsDatabase.INSTANCE.insertImagesIntoImageCollection(collectionName, imageFiles);
    }

    @Override
    public int updateRenameImageCollection(String fromName, String toName) {
        return ImageCollectionsDatabase.INSTANCE.updateRenameImageCollection(fromName, toName);
    }
}
