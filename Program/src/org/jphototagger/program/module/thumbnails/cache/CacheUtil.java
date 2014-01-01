package org.jphototagger.program.module.thumbnails.cache;

import org.jphototagger.domain.metadata.exif.ExifCacheProvider;
import org.jphototagger.iptc.IptcIgnoreCache;
import org.jphototagger.xmp.EmbeddedXmpCache;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class CacheUtil {

    public static void initCaches() {
        EmbeddedXmpCache.INSTANCE.init();
        Lookup.getDefault().lookup(ExifCacheProvider.class).clear();
        IptcIgnoreCache.INSTANCE.init();
    }

    private CacheUtil() {
    }
}
