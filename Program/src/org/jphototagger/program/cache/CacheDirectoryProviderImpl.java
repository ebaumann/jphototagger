package org.jphototagger.program.cache;

import java.io.File;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.storage.CacheDirectoryProvider;
import org.jphototagger.api.storage.PreferencesDirectoryProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = CacheDirectoryProvider.class)
public final class CacheDirectoryProviderImpl implements CacheDirectoryProvider {

    private static final String CACHE_DIRECTORY_ROOT_PATHNAME;

    static {
        PreferencesDirectoryProvider provider = Lookup.getDefault().lookup(PreferencesDirectoryProvider.class);
        File userDirectory = provider.getUserPreferencesDirectory();

        CACHE_DIRECTORY_ROOT_PATHNAME = userDirectory + File.separator + "cache";
    }

    @Override
    public File getCacheDirectory(String subdirectory) {
        String pathname = CACHE_DIRECTORY_ROOT_PATHNAME + File.separator + subdirectory;

        return ensureDirectoryExists(pathname);
    }

    private File ensureDirectoryExists(String pathname) {
        File directory = new File(pathname);

        if (!directory.isDirectory()) {
            boolean created = directory.mkdirs();

            if (!created) {
                throw new RuntimeException("Error creating cache directory '" + pathname + "'");
            }
        }

        return directory;
    }
}
