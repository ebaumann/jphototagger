package org.jphototagger.exif.cache;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.metadata.exif.ExifCacheProvider;

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
