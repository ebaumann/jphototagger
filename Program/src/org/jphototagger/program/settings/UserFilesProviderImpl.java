package org.jphototagger.program.settings;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.api.core.UserFilesProvider;
import org.jphototagger.api.file.FilenameTokens;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = UserFilesProvider.class)
public final class UserFilesProviderImpl implements UserFilesProvider {

    private static final String SETTINGS_DIRECTORY_NAME = UserSettings.INSTANCE.getSettingsDirectoryName();
    private static final File USER_DIRECTORY = new File(SETTINGS_DIRECTORY_NAME);
    private static final File PLUGIN_USER_DIRECTORY = new File(SETTINGS_DIRECTORY_NAME + File.separator + "pluginsettings");
    private static final Logger LOGGER = Logger.getLogger(UserFilesProviderImpl.class.getName());

    @Override
    public File getUserSettingsDirectory() {
        ensureDirectoryExists(USER_DIRECTORY);

        return USER_DIRECTORY;
    }

    @Override
    public File getPluginSettingsDirectory() {
        ensureDirectoryExists(PLUGIN_USER_DIRECTORY);

        return PLUGIN_USER_DIRECTORY;
    }

    private synchronized void ensureDirectoryExists(File directory) {
        if (!directory.isDirectory()) {
            boolean created = directory.mkdirs();

            if (!created) {
                LOGGER.log(Level.WARNING, "Error creating directory ''{0}''", directory);
            }
        }
    }

    @Override
    public File getDatabaseDirectory() {
        String databaseDirectoryName = UserSettings.INSTANCE.getDatabaseDirectoryName();

        return new File(databaseDirectoryName);
    }

    @Override
    public File getDatabaseBackupDirectory() {
        String databaseBackupDirectoryName = UserSettings.INSTANCE.getDatabaseBackupDirectoryName();

        return new File(databaseBackupDirectoryName);
    }

    @Override
    public File getDefaultDatabaseDirectory() {
        String defaultDatabaseDirectoryName = UserSettings.INSTANCE.getDefaultDatabaseDirectoryName();

        return new File(defaultDatabaseDirectoryName);
    }

    @Override
    public String getDatabaseFileName(FilenameTokens name) {
        return UserSettings.INSTANCE.getDatabaseFileName(name);
    }

    @Override
    public String getDatabaseBasename() {
        return UserSettings.getDatabaseBasename();
    }

    @Override
    public File getThumbnailsDirectory() {
        String thumbnailsDirectoryName = UserSettings.INSTANCE.getThumbnailsDirectoryName();

        return new File(thumbnailsDirectoryName);
    }

    @Override
    public String getThumbnailsDirectoryBasename() {
        return UserSettings.getThumbnailsDirectoryBasename();
    }
}
