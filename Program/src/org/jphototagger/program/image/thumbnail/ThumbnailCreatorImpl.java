package org.jphototagger.program.image.thumbnail;

import java.awt.Image;
import java.io.File;
import java.util.Collection;
import java.util.Set;
import org.jphototagger.api.image.ThumbnailCreationStrategy;
import org.jphototagger.api.image.ThumbnailCreator;
import org.openide.util.Lookup;
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
        Image thumbnail = null;

        if (canCreateThumbnail(file)) {
            thumbnail = ThumbnailUtil.getThumbnail(file);
        }

        if (thumbnail != null) {
            return thumbnail;
        }

        Collection<? extends ThumbnailCreator> tnCreators = Lookup.getDefault().lookupAll(ThumbnailCreator.class);

        // DO NOT USE such a loop in another implementation, this may lead to endless
        // calls between implementations
        for (ThumbnailCreator tnCreator : tnCreators) {
            if (!(tnCreator instanceof ThumbnailCreatorImpl)) {
                if (tnCreator.canCreateThumbnail(file)) {
                    thumbnail = tnCreator.createThumbnail(file);

                    if (thumbnail != null) {
                        return thumbnail;
                    }
                }
            }
        }

        return createFromEmbeddedThumbnail(file);
    }

    @Override
    public Image createFromEmbeddedThumbnail(File file) {
        Image thumbnail = null;

        if (canCreateEmbeddedThumbnail(file)) {
            thumbnail = ThumbnailUtil.getEmbeddedThumbnail(file);
        }

        if (thumbnail != null) {
            return thumbnail;
        }

        Collection<? extends ThumbnailCreator> tnCreators = Lookup.getDefault().lookupAll(ThumbnailCreator.class);

        // DO NOT USE such a loop in another implementation, this may lead to endless
        // calls between implementations
        for (ThumbnailCreator tnCreator : tnCreators) {
            if (!(tnCreator instanceof ThumbnailCreatorImpl)) {
                if (tnCreator.canCreateEmbeddedThumbnail(file)) {
                    thumbnail = tnCreator.createFromEmbeddedThumbnail(file);

                    if (thumbnail != null) {
                        return thumbnail;
                    }
                }
            }
        }

        return null;
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
    public Set<String> getSupportedFileTypeSuffixes() {
        return ThumbnailSupport.INSTANCE.getSupportedFileTypeSuffixes();
    }

    @Override
    public boolean canCreateEmbeddedThumbnail(File file) {
        return ThumbnailSupport.INSTANCE.canCreateEmbeddedThumbnail(file);
    }
}
