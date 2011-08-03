package org.jphototagger.program.serviceprovider.core;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.api.core.CacheDirectoryProvider;
import org.jphototagger.program.UserSettings;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = CacheDirectoryProvider.class)
public final class CacheDirectoryProviderImpl implements CacheDirectoryProvider {

    private static final File CACHE_DIRECTORY = new File(UserSettings.INSTANCE.getSettingsDirectoryName() + File.separator + "cache");
    private static final Logger LOGGER = Logger.getLogger(CacheDirectoryProviderImpl.class.getName());

    @Override
    public File getCacheDirectory() {
        ensureCacheDirectoryExists();

        return CACHE_DIRECTORY;
    }

    private synchronized void ensureCacheDirectoryExists() {
        if (!CACHE_DIRECTORY.isDirectory()) {
            boolean created = CACHE_DIRECTORY.mkdirs();

            if (!created) {
                LOGGER.log(Level.WARNING, "Error creating cache folder ''{0}''", CACHE_DIRECTORY);
            }
        }
    }
}
