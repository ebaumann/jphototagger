package org.jphototagger.domain.filefilter;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Elmar Baumann
 */
public interface AppFileFilterProvider {

    FileFilter getAcceptedImageFilesFileFilter();

    boolean isAcceptedImageFile(File file);
}
