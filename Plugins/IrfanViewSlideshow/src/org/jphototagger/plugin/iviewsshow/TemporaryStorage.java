package org.jphototagger.plugin.iviewsshow;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.storage.CacheDirectoryProvider;
import org.jphototagger.lib.io.FileUtil;
import org.openide.util.Lookup;

/**
 * @author  Elmar Baumann
 */
final class TemporaryStorage {

    private final String cacheDirectoryPathname;
    static final TemporaryStorage INSTANCE = new TemporaryStorage();

    private TemporaryStorage() {
        cacheDirectoryPathname = getCacheDirectoryPathname();
        createCacheDirectoryIfAbsent();
    }

    private String getCacheDirectoryPathname() {
        CacheDirectoryProvider provider = Lookup.getDefault().lookup(CacheDirectoryProvider.class);

        return provider.getCacheDirectory("IrfanViewSlideshow").getAbsolutePath();
    }

    private void createCacheDirectoryIfAbsent() {
        try {
            FileUtil.ensureDirectoryExists(new File(cacheDirectoryPathname));
        } catch (Throwable t) {
            Logger.getLogger(TemporaryStorage.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    File getNotExistingSlideshowFile() {
        String basename = cacheDirectoryPathname + File.separator + "slideshow.txt";
        File notExistingFile = FileUtil.getNotExistingFile(new File(basename));
        String filename = notExistingFile.getAbsolutePath();

        return new File(filename);
    }

    void cleanup() {
        File[] files = new File(cacheDirectoryPathname).listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            boolean deleted = file.delete();

            if (deleted) {
                Logger.getLogger(TemporaryStorage.class.getName()).log(Level.INFO, "Deleted temporary slideshow file ''{0}''", file);
            } else {
                Logger.getLogger(TemporaryStorage.class.getName()).log(Level.WARNING, "Couldn''t delete temporary slideshow file ''{0}''", file);
            }
        }
    }
}
