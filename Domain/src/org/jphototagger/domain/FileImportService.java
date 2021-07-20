package org.jphototagger.domain;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public interface FileImportService {

    void importFilesFromDirectory(File directory);
}
