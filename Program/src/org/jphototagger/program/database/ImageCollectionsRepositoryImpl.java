package org.jphototagger.program.database;

import java.io.File;
import java.util.List;

import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ImageCollectionsRepository.class)
public final class ImageCollectionsRepositoryImpl implements ImageCollectionsRepository {

    private final DatabaseImageCollections db = DatabaseImageCollections.INSTANCE;

    @Override
    public boolean deleteImageCollection(String collectionName) {
        return db.deleteImageCollection(collectionName);
    }

    @Override
    public int deleteImagesFromImageCollection(String collectionName, List<File> imageFiles) {
        return db.deleteImagesFromImageCollection(collectionName, imageFiles);
    }

    @Override
    public boolean existsImageCollection(String collectionName) {
        return db.existsImageCollection(collectionName);
    }

    @Override
    public List<String> getAllImageCollectionNames() {
        return db.getAllImageCollectionNames();
    }

    @Override
    public List<ImageCollection> getAllImageCollections() {
        return db.getAllImageCollections();
    }

    @Override
    public int getImageCollectionCount() {
        return db.getImageCollectionCount();
    }

    @Override
    public int getImageCountOfAllImageCollections() {
        return db.getImageCountOfAllImageCollections();
    }

    @Override
    public List<File> getImageFilesOfImageCollection(String collectionName) {
        return db.getImageFilesOfImageCollection(collectionName);
    }

    @Override
    public boolean insertImageCollection(String collectionName, List<File> imageFiles) {
        return db.insertImageCollection(collectionName, imageFiles);
    }

    @Override
    public boolean insertImageCollection(ImageCollection collection) {
        return db.insertImageCollection(collection);
    }

    @Override
    public boolean insertImagesIntoImageCollection(String collectionName, List<File> imageFiles) {
        return db.insertImagesIntoImageCollection(collectionName, imageFiles);
    }

    @Override
    public int updateRenameImageCollection(String fromName, String toName) {
        return db.updateRenameImageCollection(fromName, toName);
    }
}
