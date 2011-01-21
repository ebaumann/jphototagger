package org.jphototagger.program.cache;

import java.io.File;

/**
 * Base class for container object contained in the caches
 *
 * @author Martin Pohlack
 */
public abstract class CacheIndirection {
    public int  usageTime;
    public File file;

    public CacheIndirection(File _file) {
        if (_file == null) {
            throw new NullPointerException("_file == null");
        }

        file = _file;
    }

    public abstract boolean isEmpty();
}
