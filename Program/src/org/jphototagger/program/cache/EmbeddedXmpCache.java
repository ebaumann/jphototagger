
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.jphototagger.program.cache;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.UserSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Level;

import java.util.logging.Logger;
import org.jphototagger.lib.io.FileUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class EmbeddedXmpCache {
    private static final File CACHE_DIR = new File(UserSettings.getDatabaseBasename() + File.separator
                                              + "EmbeddedXmpCache");
    private static final Logger LOGGER = Logger.getLogger(EmbeddedXmpCache.class.getName());

    public static void cacheXmp(File imageFile, String xmpAsString) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (xmpAsString == null) {
            throw new NullPointerException("xmpAsString == null");
        }

        File cacheFile = getCacheFile(imageFile);
        FileOutputStream fos = null;

        try {
            LOGGER.log(Level.FINE, "Caching embedded XMP of image file ''{0}'' into ''{1}''", new Object[]{imageFile, cacheFile});
            fos = new FileOutputStream(cacheFile);
            fos.write(xmpAsString.getBytes());
        } catch (Throwable ex) {
            AppLogger.logSevere(EmbeddedXmpCache.class, ex);
            cacheFile.delete();
        } finally {
            FileUtil.close(fos);
        }
    }

    public static boolean containsXmp(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        return getCacheFile(imageFile).isFile();
    }

    public static boolean containsUpToDateXmp(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (!containsXmp(imageFile)) {
            return false;
        }

        File cacheFile = getCacheFile(imageFile);
        long timestampImageFile = imageFile.lastModified();
        long timestampCachedFile = cacheFile.lastModified();

        return timestampCachedFile >= timestampImageFile;
    }

    public static void ensureCacheDiretoryExists() {
        if (!CACHE_DIR.isDirectory()) {
            try {
                FileUtil.ensureDirectoryExists(CACHE_DIR);
            } catch (Throwable ex) {
                AppLogger.logSevere(EmbeddedXmpCache.class, ex);
            }
        }
    }

    private static File getCacheFile(File imageFile) {
        return new File(CACHE_DIR + File.separator + CacheFileUtil.getMd5Filename(imageFile) + ".xml");
    }

    private EmbeddedXmpCache() {}
}
