package org.jphototagger.program.cache;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.domain.event.listener.DatabaseImageFilesListenerAdapter;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class IptcIgnoreCache extends DatabaseImageFilesListenerAdapter {

    private final File CACHE_DIR = new File(UserSettings.INSTANCE.getDatabaseDirectoryName() + File.separator + "IptcIgnoreCache");
    private static final Logger LOGGER = Logger.getLogger(IptcIgnoreCache.class.getName());
    private static final String FILE_CONTENT= "";
    public static final IptcIgnoreCache INSTANCE = new IptcIgnoreCache();

    public boolean isIgnore(File imageFile) {
        if (!UserSettings.INSTANCE.isDisplayIptc()) {
            return true;
        }

        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        boolean ignore = containsUpToDateIgnoreInfo(imageFile);

        if (ignore) {
            LOGGER.log(Level.FINEST, "IPTC Ignore Cache: Ignore reading IPTC from image file ''{0}''", imageFile);
        }

        return ignore;
    }

    public void setIgnore(File imageFile, boolean ignore) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        File cacheFile = getCacheFile(imageFile);

        if (ignore) {
            ignore(imageFile, cacheFile);
        } else {
            unIgnore(imageFile, cacheFile);
        }

    }

    private void ignore(File imageFile, File cacheFile) {
        try {
            LOGGER.log(Level.FINEST,
                    "IPTC Ignore Cache: Ignore reading IPTC from image file ''{0}'' (Creating ''{1}'')",
                    new Object[]{imageFile, cacheFile});
            FileUtil.writeStringAsFile(FILE_CONTENT, cacheFile);
            FileUtil.touch(cacheFile, imageFile);
        } catch (Throwable throwable) {
            AppLogger.logSevere(IptcIgnoreCache.class, throwable);
        }
    }

    private void unIgnore(File imageFile, File cacheFile) {
        if (cacheFile.isFile()) {
            LOGGER.log(Level.FINEST,
                    "IPTC Ignore Cache: Don''t ignore reading IPTC from image file ''{0}'' (Deleting ''{1}'')",
                    new Object[]{imageFile, cacheFile});
            cacheFile.delete();
        }
    }

    private boolean containsIgnoreInfo(File imageFile) {
        return getCacheFile(imageFile).isFile();
    }

    private boolean containsUpToDateIgnoreInfo(File imageFile) {
        if (!containsIgnoreInfo(imageFile)) {
            return false;
        }

        File cacheFile = getCacheFile(imageFile);
        long timestampImageFile = imageFile.lastModified();
        long timestampCachedFile = cacheFile.lastModified();

        return timestampCachedFile == timestampImageFile;
    }

    public void ensureCacheDiretoryExists() {
        if (!CACHE_DIR.isDirectory()) {
            try {
                LOGGER.log(Level.FINEST, "IPTC Ignore Cache: Creating cache directory ''{0}''", CACHE_DIR);
                FileUtil.ensureDirectoryExists(CACHE_DIR);
            } catch (Throwable ex) {
                AppLogger.logSevere(IptcIgnoreCache.class, ex);
            }
        }
    }
    /**
     * Removes all files from the cache directory.
     *
     * @return count of deleted files
     */
    public int clear() {
        File[] cacheFiles = CACHE_DIR.listFiles();

        if (cacheFiles == null || cacheFiles.length == 0) {
            return 0;
        }

        LOGGER.log(Level.INFO, "IPTC Ignore Cache: Deleting all cache files in directory ''{0}''", CACHE_DIR);

        int deleteCount = 0;

        for (File cacheFile : cacheFiles) {
            boolean deleted = cacheFile.delete();

            if (deleted) {
                deleteCount++;
            } else {
                LOGGER.log(Level.WARNING, "IPTC Ignore Cache: Couldn't delete cache file ''{0}''", cacheFile);
            }
        }

        return deleteCount;
    }

    /**
     *
     * @return count of cached files
     */
    public int getSize() {
        File[] cacheFiles = CACHE_DIR.listFiles();

        return cacheFiles == null
                ? 0
                : cacheFiles.length;
    }


    private File getCacheFile(File imageFile) {
        return new File(CACHE_DIR + File.separator + CacheFileUtil.getMd5Filename(imageFile));
    }


    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {
        File oldCacheFile = getCacheFile(oldImageFile);

        if (oldCacheFile.exists()) {
            File newCacheFile = getCacheFile(newImageFile);

            LOGGER.log(Level.FINEST,
                    "IPTC Ignore Cache: Renaming IPTC ignore info for image file ''{0}'' renamed to ''{1}'' from ''{2}'' into ''{3}''",
                    new Object[]{oldImageFile, newImageFile, oldCacheFile, newCacheFile});

            oldCacheFile.renameTo(newCacheFile);
        }
    }

    @Override
    public void imageFileDeleted(File imageFile) {
        File cacheFile = getCacheFile(imageFile);

        if (cacheFile.exists()) {
            unIgnore(imageFile, cacheFile);
        }
    }

    private IptcIgnoreCache() {
        DatabaseImageFiles.INSTANCE.addListener(this);
    }
}
