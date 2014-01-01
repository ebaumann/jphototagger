package org.jphototagger.program.module.thumbnails.cache;

import java.awt.Image;
import java.io.File;
import org.jphototagger.domain.thumbnails.ThumbnailProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailProvider.class)
public final class ThumbnailProviderImpl implements ThumbnailProvider {

    @Override
    public Image getThumbnail(File imageFile) {
        return imageFile == null
                ? null
                : ThumbnailsDb.findThumbnail(imageFile);
    }
}
