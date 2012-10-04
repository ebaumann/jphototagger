package org.jphototagger.api.file;

import java.io.File;
import org.jphototagger.api.collections.PositionProvider;
import org.jphototagger.api.component.DisplayNameProvider;

/**
 * @author Elmar Baumann
 */
public interface FileRenameStrategy extends DisplayNameProvider, PositionProvider {

    /**
     * E.g. for resetting a counter
     */
    void init();

    /**
     * @param sourceFile
     * @param targetDirectoryPath
     * @return file with new name within the target directory
     */
    File suggestNewFile(File sourceFile, String targetDirectoryPath);
}
