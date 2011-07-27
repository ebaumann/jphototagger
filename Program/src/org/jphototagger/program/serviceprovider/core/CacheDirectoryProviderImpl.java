package org.jphototagger.program.serviceprovider.core;

import java.io.File;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.services.core.CacheDirectoryProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class CacheDirectoryProviderImpl implements CacheDirectoryProvider {

    private static final File CACHE_DIRECTORY = new File(UserSettings.INSTANCE.getDatabaseDirectoryName() + File.separator + "plugincache");

    @Override
    public File getCacheDirectory() {
        ensureCacheDirectoryExists();

        return CACHE_DIRECTORY;
    }

    private synchronized void ensureCacheDirectoryExists() {
        if (!CACHE_DIRECTORY.isDirectory()) {
            boolean created = CACHE_DIRECTORY.mkdirs();

            if (!created) {
                AppLogger.logWarning(CacheDirectoryProviderImpl.class,
                        "JptCacheDirectoryProvider.Error.CreatingCacheDirectory", CACHE_DIRECTORY);
            }
        }
    }
}
