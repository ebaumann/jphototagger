package org.jphototagger.program.cache;

import com.adobe.xmp.properties.XMPPropertyInfo;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.UserSettings;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

import java.util.logging.Logger;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.adapter.DatabaseImageFilesListenerAdapter;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class EmbeddedXmpCache extends DatabaseImageFilesListenerAdapter {
    private final File CACHE_DIR = new File(UserSettings.INSTANCE.getDatabaseDirectoryName() + File.separator + "EmbeddedXmpCache");
    private static final Logger LOGGER = Logger.getLogger(EmbeddedXmpCache.class.getName());
    private final String EMPTY_XMP = getEmptyXmp();
    public static final EmbeddedXmpCache INSTANCE = new EmbeddedXmpCache();

    public void cacheXmp(File imageFile, String xmpAsString) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (xmpAsString == null) {
            throw new NullPointerException("xmpAsString == null");
        }

        File cacheFile = getCacheFile(imageFile);

        try {
            LOGGER.log(Level.FINE, "Caching embedded XMP of image file ''{0}'' into ''{1}''", new Object[]{imageFile, cacheFile});
            FileUtil.writeStringAsFile(cacheFile, xmpAsString);
        } catch (Throwable ex) {
            AppLogger.logSevere(EmbeddedXmpCache.class, ex);
            cacheFile.delete();
        }
    }

    public void cacheXmpIfNotUpToDate(File imageFile, String xmpAsString) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (xmpAsString == null) {
            throw new NullPointerException("xmpAsString == null");
        }

        if (!containsUpToDateXmp(imageFile)) {
            LOGGER.log(Level.FINEST, "Updating embedded XMP cache file of image file ''{0}''", new Object[] { imageFile });
            cacheXmp(imageFile, xmpAsString);
        }
    }

    public boolean containsXmp(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        return getCacheFile(imageFile).isFile();
    }

    public boolean containsUpToDateXmp(File imageFile) {
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

    /**
     *
     * @param  imageFile
     * @return           Cached XMP as string or null if the cache does not contain
     *                   a XMP string for that image file or on errors
     */
    public String getCachedXmp(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        File cacheFile = getCacheFile(imageFile);

        if (!cacheFile.isFile()) {
            return null;
        }

        try {
            LOGGER.log(Level.INFO, "Reading embedded XMP cache of image file ''{0}'' from cache file ''{1}''",
                       new Object[] { imageFile, cacheFile });
            return FileUtil.getContentAsString(cacheFile, "UTF-8");
        } catch (Throwable throwable) {
            AppLogger.logSevere(EmbeddedXmpCache.class, throwable);
        }

        return null;
    }

    public void deleteCachedXmp(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        File cacheFile = getCacheFile(imageFile);

        if (cacheFile.isFile()) {
            LOGGER.log(Level.FINEST, "Deleting embedded XMP cache file ''{0}'' of image file ''{1}''",
                    new Object[] { cacheFile, imageFile });
            cacheFile.delete();
        }
    }

    public void renameCachedXmp(File oldImageFile, File newImageFile) {
        if (!containsXmp(oldImageFile)) {
            return;
        }

        File oldCacheFile = getCacheFile(oldImageFile);
        File newCacheFile = getCacheFile(newImageFile);

        LOGGER.log(
            Level.FINEST,
            "Renaming embedded XMP cache file ''{0}'' of renamed image file ''{1}'' to cache file ''{2}'' of new image file ''{3}''",
            new Object[] { oldCacheFile, oldImageFile, newCacheFile, newImageFile });

        if (newCacheFile.isFile()) {
            newCacheFile.delete();
        }

        oldCacheFile.renameTo(newCacheFile);
    }

    public List<XMPPropertyInfo> getXmpPropertyInfos(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        String xmpAsString = "";

        if (containsUpToDateXmp(imageFile)) {
            xmpAsString = getCachedXmp(imageFile);
        } else {
            xmpAsString = XmpMetadata.getEmbeddedXmpAsString(imageFile);

            if (xmpAsString == null) {
                cacheXmp(imageFile, EMPTY_XMP);
            } else {
                cacheXmp(imageFile, xmpAsString);
            }
        }

        return XmpMetadata.getPropertyInfosOfXmpString(xmpAsString);
    }

    void ensureCacheDiretoryExists() {
        if (!CACHE_DIR.isDirectory()) {
            try {
                LOGGER.log(Level.INFO, "Creating cache directory ''{0}''", CACHE_DIR);
                FileUtil.ensureDirectoryExists(CACHE_DIR);
            } catch (Throwable ex) {
                AppLogger.logSevere(EmbeddedXmpCache.class, ex);
            }
        }
    }

    private File getCacheFile(File imageFile) {
        return new File(CACHE_DIR + File.separator + CacheFileUtil.getMd5Filename(imageFile) + ".xml");
    }

    private String getEmptyXmp() {
        InputStream is = null;

        try {
            is = EmbeddedXmpCache.class.getResourceAsStream("Empty.xmp");
            return StringUtil.convertStreamToString(is, "UTF-8");
        } catch (Throwable throwable) {
            AppLogger.logSevere(EmbeddedXmpCache.class, throwable);
        } finally {
            FileUtil.close(is);
        }

        return "";
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {
        renameCachedXmp(oldImageFile, newImageFile);
    }

    @Override
    public void imageFileDeleted(File imageFile) {
        deleteCachedXmp(imageFile);
    }

    private EmbeddedXmpCache() {
        DatabaseImageFiles.INSTANCE.addListener(this);
    }
}
