package org.jphototagger.program.serviceprovider;

import java.io.File;
import org.jphototagger.program.UserSettings;
import org.jphototagger.services.core.CacheDirectoryProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class JptCacheDirectoryProvider implements CacheDirectoryProvider {

    private static final File CACHE_DIRECTORY = new File(UserSettings.INSTANCE.getDatabaseDirectoryName());

    @Override
    public File getCacheDirectory() {
        return CACHE_DIRECTORY;
    }
}
