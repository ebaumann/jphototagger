package org.jphototagger.api.core;

import java.io.File;
import org.jphototagger.api.file.Filename;

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

    String getDatabaseFileName(Filename name);

    String getDatabaseBasename();

    File getThumbnailsDirectory();

    String getThumbnailsDirectoryBasename();
}
