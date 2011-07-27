package org.jphototagger.program.serviceprovider.core;

import java.io.File;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.services.core.UserDirectoryProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class UserDirectoryServiceProviderImpl implements UserDirectoryProvider {

    private static final File USER_DIRECTORY = new File(UserSettings.INSTANCE.getSettingsDirectoryName() + File.separator + "pluginsettings");

    @Override
    public File getUserDirectory() {
        ensureUserDirectoryExists();

        return USER_DIRECTORY;
    }

    private synchronized void ensureUserDirectoryExists() {
        if (!USER_DIRECTORY.isDirectory()) {
            boolean created = USER_DIRECTORY.mkdirs();

            if (!created) {
                AppLogger.logWarning(UserDirectoryServiceProviderImpl.class,
                        "UserDirectoryServiceProvider.Error.CreatingUserDirectory", USER_DIRECTORY);
            }
        }
    }
}
