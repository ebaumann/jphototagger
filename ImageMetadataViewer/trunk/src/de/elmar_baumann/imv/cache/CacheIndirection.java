package de.elmar_baumann.imv.cache;

import java.io.File;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public abstract class CacheIndirection {
    public int usageTime;
    final public File file;

    public CacheIndirection(File _file) {
        file = _file;
    }

    public abstract boolean isEmpty();
}