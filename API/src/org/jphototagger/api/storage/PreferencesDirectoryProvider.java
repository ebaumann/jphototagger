package org.jphototagger.api.storage;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public interface PreferencesDirectoryProvider {

    File getPluginPreferencesDirectory();

    File getUserPreferencesDirectory();
}
