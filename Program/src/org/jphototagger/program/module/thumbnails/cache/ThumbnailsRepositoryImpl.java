package org.jphototagger.program.module.thumbnails.cache;

import java.awt.Image;
import java.io.File;
import java.util.Set;
import org.jphototagger.domain.repository.ThumbnailsRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailsRepository.class)
public final class ThumbnailsRepositoryImpl implements ThumbnailsRepository {

    @Override
    public boolean deleteThumbnail(File imageFile) {
        return ThumbnailsDb.deleteThumbnail(imageFile);
    }

    @Override
    public boolean existsThumbnail(File imageFile) {
        return ThumbnailsDb.existsThumbnail(imageFile);
    }

    @Override
    public Image findThumbnail(File imageFile) {
        return ThumbnailsDb.findThumbnail(imageFile);
    }

    @Override
    public boolean renameThumbnail(File fromImageFile, File toImageFile) {
        return ThumbnailsDb.renameThumbnail(fromImageFile, toImageFile);
    }

    @Override
    public void insertThumbnail(Image thumbnail, File imageFile) {
        ThumbnailsDb.insertThumbnail(thumbnail, imageFile);
    }

    @Override
    public boolean hasUpToDateThumbnail(File imageFile) {
        return ThumbnailsDb.hasUpToDateThumbnail(imageFile);
    }

    @Override
    public Set<String> getImageFilenames() {
        return ThumbnailsDb.getImageFilenames();
    }

    @Override
    public void compact() {
        ThumbnailsDb.compact();
    }
}
