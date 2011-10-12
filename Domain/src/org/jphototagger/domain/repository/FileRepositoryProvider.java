package org.jphototagger.domain.repository;

import java.io.File;

import org.jphototagger.api.file.FilenameTokens;

/**
 * @author Elmar Baumann
 */
public interface FileRepositoryProvider {

    static final String KEY_FILE_REPOSITORY_DIRECTORY = "UserSettings.DatabaseDirectoryName";

    File getFileRepositoryDirectory();

    File getDefaultFileRepositoryDirectory();

    String getFileRepositoryFileName(FilenameTokens filenameTokens);
}
