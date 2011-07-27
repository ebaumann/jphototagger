package org.jphototagger.program.serviceprovider.core;

import java.awt.Image;
import java.io.File;
import org.jphototagger.program.cache.ThumbnailCache;
import org.jphototagger.services.core.ThumbnailProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ThumbnailProviderImpl implements ThumbnailProvider {

    @Override
    public Image getThumbnail(File imageFile) {
        return imageFile == null
                ? null
                : ThumbnailCache.INSTANCE.getThumbnail(imageFile);
    }
}
