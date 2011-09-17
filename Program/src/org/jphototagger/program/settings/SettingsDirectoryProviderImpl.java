package org.jphototagger.program.settings;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.storage.SettingsDirectoryProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = SettingsDirectoryProvider.class)
public final class SettingsDirectoryProviderImpl implements SettingsDirectoryProvider {

    private static final String SETTINGS_DIRECTORY_NAME = UserPreferences.INSTANCE.getSettingsDirectoryName();
    private static final File USER_DIRECTORY = new File(SETTINGS_DIRECTORY_NAME);
    private static final File PLUGIN_USER_DIRECTORY = new File(SETTINGS_DIRECTORY_NAME + File.separator + "pluginsettings");
    private static final Logger LOGGER = Logger.getLogger(SettingsDirectoryProviderImpl.class.getName());

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
}
