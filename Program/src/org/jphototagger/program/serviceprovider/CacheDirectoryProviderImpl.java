package org.jphototagger.program.serviceprovider;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.api.storage.CacheDirectoryProvider;
import org.jphototagger.api.storage.UserFilesProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = CacheDirectoryProvider.class)
public final class CacheDirectoryProviderImpl implements CacheDirectoryProvider {

    private static final File CACHE_DIRECTORY;
    private static final Logger LOGGER = Logger.getLogger(CacheDirectoryProviderImpl.class.getName());

    static {
        UserFilesProvider provider = Lookup.getDefault().lookup(UserFilesProvider.class);
        File userDirectory = provider.getUserSettingsDirectory();
        CACHE_DIRECTORY = new File(userDirectory + File.separator + "cache");
    }

    @Override
    public File getCacheDirectory() {
        ensureCacheDirectoryExists();

        return CACHE_DIRECTORY;
    }

    private synchronized void ensureCacheDirectoryExists() {
        if (!CACHE_DIRECTORY.isDirectory()) {
            boolean created = CACHE_DIRECTORY.mkdirs();

            if (!created) {
                LOGGER.log(Level.WARNING, "Error creating cache directory ''{0}''", CACHE_DIRECTORY);
            }
        }
    }
}
