package org.jphototagger.program.serviceprovider.core;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.api.core.UserDirectoryProvider;
import org.jphototagger.program.UserSettings;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class UserDirectoryServiceProviderImpl implements UserDirectoryProvider {

    private static final File USER_DIRECTORY = new File(UserSettings.INSTANCE.getSettingsDirectoryName() + File.separator + "pluginsettings");
    private static final Logger LOGGER = Logger.getLogger(UserDirectoryServiceProviderImpl.class.getName());

    @Override
    public File getUserDirectory() {
        ensureUserDirectoryExists();

        return USER_DIRECTORY;
    }

    private synchronized void ensureUserDirectoryExists() {
        if (!USER_DIRECTORY.isDirectory()) {
            boolean created = USER_DIRECTORY.mkdirs();

            if (!created) {
                LOGGER.log(Level.WARNING, "Error creating user folder ''{0}''", USER_DIRECTORY);
            }
        }
    }
}
