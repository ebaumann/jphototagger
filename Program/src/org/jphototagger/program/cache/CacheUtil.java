package org.jphototagger.program.cache;

import org.jphototagger.exif.cache.ExifCache;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class CacheUtil {

    public static void initCaches() {
        EmbeddedXmpCache.INSTANCE.init();
        ExifCache.INSTANCE.init();
        IptcIgnoreCache.INSTANCE.init();
    }

    private CacheUtil() {
    }
}
