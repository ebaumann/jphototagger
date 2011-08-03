package org.jphototagger.program.serviceprovider.core;

import java.awt.Image;
import java.io.File;

import org.jphototagger.api.image.ThumbnailProvider;
import org.jphototagger.program.cache.ThumbnailCache;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailProvider.class)
public final class ThumbnailProviderImpl implements ThumbnailProvider {

    @Override
    public Image getThumbnail(File imageFile) {
        return imageFile == null
                ? null
                : ThumbnailCache.INSTANCE.getThumbnail(imageFile);
    }
}
