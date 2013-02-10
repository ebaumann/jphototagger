package org.jphototagger.exif.cache;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.storage.CacheDirectoryProvider;
import org.jphototagger.domain.metadata.exif.event.ExifCacheClearedEvent;
import org.jphototagger.domain.metadata.exif.event.ExifCacheFileDeletedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileDeletedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileMovedEvent;
import org.jphototagger.exif.ExifTags;
import org.jphototagger.lib.io.DeleteOutOfDateFilesInDirectoryThread;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.filefilter.AcceptAllFilesFilter;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ExifCache {

    private static final Logger LOGGER = Logger.getLogger(ExifCache.class.getName()); // Has to be instanciated before INSTANCE!
    public static final ExifCache INSTANCE = new ExifCache();
    private static final String PREF_KEY_STRUCTURE_VERSION = "org.jphototagger.exif.ExifDataStructureVersion";
    private static final int STRUCTURE_VERSION = 1;
    private final File CACHE_DIR;

    public void cacheExifTags(File imageFile, ExifTags exifTags) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        if (exifTags == null) {
            throw new NullPointerException("exifTags == null");
        }
        File cacheFile = getExifTagsCacheFile(imageFile);
        try {
            LOGGER.log(Level.FINEST, "EXIF Cache: Caching EXIF of image file ''{0}'' into cache file ''{1}''",
                    new Object[]{imageFile, cacheFile});
            exifTags.writeToFile(cacheFile);
            FileUtil.touch(cacheFile, imageFile);
        } catch (Throwable ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            boolean deleted = cacheFile.delete();
            if (deleted) {
                EventBus.publish(new ExifCacheFileDeletedEvent(this, imageFile, cacheFile));
            }
        }
    }

    public boolean containsUpToDateExifTags(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        if (!isCached(imageFile)) {
            return false;
        }
        File cacheFile = getExifTagsCacheFile(imageFile);
        long timestampImageFile = imageFile.lastModified();
        long timestampCachedFile = cacheFile.lastModified();
        return timestampCachedFile == timestampImageFile;
    }

    private boolean isCached(File imageFile) {
        return getExifTagsCacheFile(imageFile).isFile();
    }

    /**
     *
     * @param  imageFile
     * @return           Cached ExifTags or null if the cache does not contain
     *                   tags for that image file or on errors
     */
    public ExifTags getCachedExifTags(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        File cacheFile = getExifTagsCacheFile(imageFile);
        if (!cacheFile.isFile()) {
            return null;
        }
        try {
            LOGGER.log(Level.FINEST, "EXIF Cache: Reading EXIF cache file of image file ''{0}'' from cache file ''{1}''",
                    new Object[]{imageFile, cacheFile});
            return ExifTags.readFromFile(cacheFile);
        } catch (Throwable ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            deleteCachedExifTags(imageFile);
            return null;
        }
    }

    private void deleteCachedExifTags(File imageFile) {
        File cacheFile = getExifTagsCacheFile(imageFile);
        if (cacheFile.isFile()) {
            LOGGER.log(Level.FINEST, "EXIF Cache: Deleting EXIF cache file ''{0}'' of image file ''{1}''",
                    new Object[]{cacheFile, imageFile});
            boolean deleted = cacheFile.delete();
            if (deleted) {
                EventBus.publish(new ExifCacheFileDeletedEvent(this, imageFile, cacheFile));
            }
        }
    }

    private void renameCachedExifTags(File oldImageFile, File newImageFile) {
        if (isCached(oldImageFile)) {
            File oldCacheFile = getExifTagsCacheFile(oldImageFile);
            File newCacheFile = getExifTagsCacheFile(newImageFile);
            LOGGER.log(
                    Level.FINEST,
                    "EXIF Cache: Renaming EXIF cache file ''{0}'' of renamed image file ''{1}'' to cache file ''{2}'' of new image file ''{3}''",
                    new Object[]{oldCacheFile, oldImageFile, newCacheFile, newImageFile});
            if (newCacheFile.isFile()) {
                newCacheFile.delete();
            }
            oldCacheFile.renameTo(newCacheFile);
        }
    }

    /**
     * Removes all files from the cache directory.
     *
     * @return count of deleted files
     */
    int clear() {
        File[] cacheFiles = CACHE_DIR.listFiles();
        if (cacheFiles == null || cacheFiles.length == 0) {
            return 0;
        }
        LOGGER.log(Level.INFO, "EXIF Cache: Deleting all cache files in directory ''{0}''", CACHE_DIR);
        int deleteCount = 0;
        for (File cacheFile : cacheFiles) {
            boolean deleted = cacheFile.delete();
            if (deleted) {
                deleteCount++;
            } else {
                LOGGER.log(Level.WARNING, "EXIF Cache: Couldn''t delete cache file ''{0}''", cacheFile);
            }
        }
        EventBus.publish(new ExifCacheClearedEvent(this, deleteCount));
        return deleteCount;
    }

    private File getExifTagsCacheFile(File imageFile) {
        return new File(CACHE_DIR + File.separator + FileUtil.getMd5FilenameOfAbsolutePath(imageFile) + ".xml");
    }

    @EventSubscriber(eventClass = ImageFileMovedEvent.class)
    public void imageFileMoved(ImageFileMovedEvent event) {
        File oldImageFile = event.getOldImageFile();
        File newImageFile = event.getNewImageFile();
        renameCachedExifTags(oldImageFile, newImageFile);
    }

    @EventSubscriber(eventClass = ImageFileDeletedEvent.class)
    public void imageFileRemoved(ImageFileDeletedEvent event) {
        File deletedImageFile = event.getImageFile();
        deleteCachedExifTags(deletedImageFile);
    }

    public void init() {
        AnnotationProcessor.process(this);
    }

    private ExifCache() {
        CACHE_DIR = lookupCacheDirectory();
        ensureCacheDiretoryExists();
        checkDataStructureVersion();
        deleteOldCachedFiles();
    }

    private File lookupCacheDirectory() {
        CacheDirectoryProvider provider = Lookup.getDefault().lookup(CacheDirectoryProvider.class);
        return provider.getCacheDirectory("ExifCache");
    }

    private void ensureCacheDiretoryExists() {
        if (!CACHE_DIR.isDirectory()) {
            try {
                LOGGER.log(Level.FINEST, "EXIF Cache: Creating cache directory ''{0}''", CACHE_DIR);
                FileUtil.ensureDirectoryExists(CACHE_DIR);
            } catch (Throwable ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    private void deleteOldCachedFiles() {
        long dayInMilliseconds = 86400000;
        long maxAgeInMilliseconds = 30 * dayInMilliseconds;
        new DeleteOutOfDateFilesInDirectoryThread(CACHE_DIR, AcceptAllFilesFilter.INSTANCE, maxAgeInMilliseconds).start();
    }

    private void checkDataStructureVersion() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean hasVersion = prefs.containsKey(PREF_KEY_STRUCTURE_VERSION);
        boolean hasOlderVersion = hasVersion && STRUCTURE_VERSION > prefs.getInt(PREF_KEY_STRUCTURE_VERSION);
        if (!hasVersion || hasOlderVersion) {
            LOGGER.log(Level.INFO, "Clearing EXIF cache due structural changes in data types");
            clear();
            prefs.setInt(PREF_KEY_STRUCTURE_VERSION, STRUCTURE_VERSION);
        }
    }
}
