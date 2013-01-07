package org.jphototagger.findduplicates;

import java.io.File;
import java.util.Collection;

/**
 * @author Elmar Baumann
 */
public interface FileDuplicatesListener {

    void searchStarted();

    void searchFinished(boolean wasCancelled);

    /**
     * @param duplicates minimum size: 2 files
     */
    void duplicatesFound(Collection<? extends File> duplicates);
}
