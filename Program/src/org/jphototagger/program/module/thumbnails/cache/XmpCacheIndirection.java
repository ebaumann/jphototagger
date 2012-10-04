package org.jphototagger.program.module.thumbnails.cache;

import java.io.File;
import org.jphototagger.domain.metadata.xmp.Xmp;

/**
 *
 * @author Martin Pohlack
 */
public class XmpCacheIndirection extends CacheIndirection {

    Xmp xmp;

    public XmpCacheIndirection(File _file) {
        super(_file);
        xmp = null;
    }

    @Override
    public boolean isEmpty() {
        return xmp == null;
    }
}
