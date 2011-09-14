package org.jphototagger.program.image.thumbnail;

import java.awt.Image;
import java.io.File;
import java.util.Set;

import org.jphototagger.api.image.ThumbnailCreator;
import org.openide.util.lookup.ServiceProvider;

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
    public boolean canCreateEmbeddedThumbnail(File file) {
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
