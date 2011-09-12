package org.jphototagger.domain.repository;

import java.awt.Image;
import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ThumbnailsRepository {

    boolean deleteThumbnail(File imageFile);

    boolean existsThumbnail(File imageFile);

    Image findThumbnail(File imageFile);

    File findThumbnailFile(File imageFile);

    boolean renameThumbnail(File fromImageFile, File toImageFile);

    void writeThumbnail(Image thumbnail, File imageFile);
}
