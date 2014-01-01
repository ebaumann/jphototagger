package org.jphototagger.exif.cache;

import org.jphototagger.domain.metadata.exif.ExifCacheProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ExifCacheProvider.class)
public final class ExifCacheProviderImpl implements ExifCacheProvider {

    @Override
    public void init() {
        ExifCache.INSTANCE.init();
    }

    @Override
    public int clear() {
        return ExifCache.INSTANCE.clear();
    }
}
