package org.jphototagger.api.storage;

import java.io.File;

import org.jphototagger.api.file.FilenameTokens;

/**
 *
 * @author Elmar Baumann
 */
public interface UserFilesProvider {

    File getPluginSettingsDirectory();

    File getUserSettingsDirectory();

    File getDatabaseDirectory();

    File getDefaultDatabaseDirectory();

    File getDatabaseBackupDirectory();

    String getDatabaseFileName(FilenameTokens name);

    String getDatabaseBasename();

    File getThumbnailsDirectory();

    String getThumbnailsDirectoryBasename();
}
