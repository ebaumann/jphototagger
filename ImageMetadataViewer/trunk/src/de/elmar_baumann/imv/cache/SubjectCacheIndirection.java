package de.elmar_baumann.imv.cache;

import java.io.File;
import java.util.List;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class SubjectCacheIndirection extends CacheIndirection {
    public List<String> subjects;

    public SubjectCacheIndirection(File _file) {
        super(_file);
        subjects = null;
    }

    @Override
    public boolean isEmpty() {
        return subjects == null;
    }
}
