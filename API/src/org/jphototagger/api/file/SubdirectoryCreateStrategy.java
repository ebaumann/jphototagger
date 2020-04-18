package org.jphototagger.api.file;

import java.io.File;
import org.jphototagger.api.collections.PositionProvider;
import org.jphototagger.api.component.DisplayNameProvider;

/**
 * Strategy for creating a subdirectory.
 *
 * @author Elmar Baumann
 */
public interface SubdirectoryCreateStrategy extends DisplayNameProvider, PositionProvider {

    /**
     * @param file file, based on which (or it's metadata) the subdirectory name
     *             will be suggested
     *
     * @return name suggestion, where {@link File#pathSeparator} should be
     *         handled in a way, that the following substring is another
     *         subdirectory. E.g. if the path separator is "/" and the
     *         suggestion "dir/subdir", two subdirectories should be created:
     *         "dir" and below that "subdir".
     */
    String suggestSubdirectoryName(File file);

    /**
     * @return true, if the strategy is user defined (i. e. not provided through
     *         JPhotoTagger)
     */
    boolean isUserDefined();
}
