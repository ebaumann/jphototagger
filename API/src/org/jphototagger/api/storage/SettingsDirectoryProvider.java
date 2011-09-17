package org.jphototagger.api.storage;

import java.io.File;

/**
 *
 * @author Elmar Baumann
 */
public interface SettingsDirectoryProvider {

    File getPluginSettingsDirectory();

    File getUserSettingsDirectory();
}
