package de.elmar_baumann.imv.cache;

import de.elmar_baumann.imv.data.Xmp;
import java.io.File;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class XmpCacheIndirection extends CacheIndirection {
    public Xmp xmp;

    public XmpCacheIndirection(File _file) {
        super(_file);
        xmp = null;
    }

    @Override
    public boolean isEmpty() {
        return xmp == null;
    }
}
