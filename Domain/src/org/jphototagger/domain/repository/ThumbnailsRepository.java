package org.jphototagger.domain.repository;

import java.awt.Image;
import java.io.File;
import java.util.Set;

/**
 * @author Elmar Baumann
 */
public interface ThumbnailsRepository {

    void insertThumbnail(Image thumbnail, File imageFile);

    Image findThumbnail(File imageFile);

    boolean existsThumbnail(File imageFile);

    boolean hasUpToDateThumbnail(File imageFile);

    boolean renameThumbnail(File fromImageFile, File toImageFile);

    boolean deleteThumbnail(File imageFile);

    void compact();

    void close();

    void backupToDirectory(File directory);

    /**
     * @return All image filenames where a thumbnail exists
     */
    Set<String> getImageFilenames();
}
