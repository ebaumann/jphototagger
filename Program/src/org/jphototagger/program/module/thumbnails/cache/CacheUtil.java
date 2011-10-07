package org.jphototagger.program.module.thumbnails.cache;

import org.jphototagger.exif.cache.ExifCache;
import org.jphototagger.iptc.IptcIgnoreCache;
import org.jphototagger.xmp.EmbeddedXmpCache;

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
