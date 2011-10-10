package org.jphototagger.image.thumbnail;

import org.jphototagger.image.thumbnail.ThumbnailCreationStrategy;
import java.awt.Image;
import java.io.File;
import java.util.Set;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.thumbnails.ThumbnailCreator;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailCreator.class)
public final class ThumbnailCreatorImpl implements ThumbnailCreator {

    @Override
    public Image createThumbnail(File file) {
        return ThumbnailUtil.getThumbnail(file);
    }

    @Override
    public boolean canCreateThumbnail(File file) {
        return externalAppCreatesThumbnails() || ThumbnailSupport.INSTANCE.canCreateThumbnail(file);
    }

    private boolean externalAppCreatesThumbnails() {
        ThumbnailCreationStrategy creationStrategy = ThumbnailUtil.getThumbnailCreationStrategy();

        return ThumbnailCreationStrategy.EXTERNAL_APP.equals(creationStrategy);
    }

    @Override
    public Image createFromEmbeddedThumbnail(File file) {
        return ThumbnailUtil.getEmbeddedThumbnail(file);
    }

    @Override
    public boolean canCreateFromEmbeddedThumbnail(File file) {
        return ThumbnailSupport.INSTANCE.canCreateEmbeddedThumbnail(file);
    }

    @Override
    public Set<String> getAllSupportedFileTypeSuffixes() {
        return ThumbnailSupport.INSTANCE.getSupportedFileTypeSuffixes();
    }

    @Override
    public Set<String> getSupportedRawFormatFileTypeSuffixes() {
        return ThumbnailSupport.INSTANCE.getSupportedRawFormatFileTypeSuffixes();
    }
}
