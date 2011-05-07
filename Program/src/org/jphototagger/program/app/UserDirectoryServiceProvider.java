package org.jphototagger.program.app;

import java.io.File;
import org.jphototagger.program.UserSettings;
import org.jphototagger.services.UserDirectoryProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class UserDirectoryServiceProvider implements UserDirectoryProvider {

    @Override
    public File getUserDirectory() {
        String dir = UserSettings.INSTANCE.getSettingsDirectoryName();
        
        return new File(dir);
    }
}
