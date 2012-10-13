package org.jphototagger.program.module.thumbnails.cache;

import java.awt.Image;
import java.io.File;
import org.jphototagger.domain.repository.ThumbnailsRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailsRepository.class)
public final class ThumbnailsRepositoryImpl implements ThumbnailsRepository {

    @Override
    public boolean deleteThumbnail(File imageFile) {
        return PersistentThumbnails.deleteThumbnail(imageFile);
    }

    @Override
    public boolean existsThumbnail(File imageFile) {
        return PersistentThumbnails.existsThumbnail(imageFile);
    }

    @Override
    public Image findThumbnail(File imageFile) {
        return PersistentThumbnails.getThumbnail(imageFile);
    }

    @Override
    public File findThumbnailFile(File imageFile) {
        return PersistentThumbnails.getThumbnailFile(imageFile);
    }

    @Override
    public boolean renameThumbnail(File fromImageFile, File toImageFile) {
        return PersistentThumbnails.renameThumbnail(fromImageFile, toImageFile);
    }

    @Override
    public void writeThumbnail(Image thumbnail, File imageFile) {
        PersistentThumbnails.writeThumbnail(thumbnail, imageFile);
    }
}
