package org.jphototagger.program.cache;

import java.io.File;
import java.util.List;

/**
 *
 * @author Martin Pohlack
 */
public class SubjectCacheIndirection extends CacheIndirection {
    List<String> subjects;

    public SubjectCacheIndirection(File _file) {
        super(_file);
        subjects = null;
    }

    @Override
    public boolean isEmpty() {
        return subjects == null;
    }
}
