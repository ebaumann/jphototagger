package org.jphototagger.exif.cache;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.storage.CacheDirectoryProvider;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileDeletedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileMovedEvent;
import org.jphototagger.exif.ExifTags;
import org.jphototagger.lib.io.FileUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ExifCache {

    private final File CACHE_DIR;
    private static final Logger LOGGER = Logger.getLogger(ExifCache.class.getName());
    public static final ExifCache INSTANCE = new ExifCache();

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
            cacheFile.delete();
        }
    }

    public void cacheExifTagsIfNotUpToDate(File imageFile, ExifTags exifTags) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (exifTags == null) {
            throw new NullPointerException("exifTags == null");
        }

        if (!containsUpToDateExifTags(imageFile)) {
            LOGGER.log(Level.FINEST, "EXIF Cache: Updating EXIF cache file of image file ''{0}''", new Object[]{imageFile});
            cacheExifTags(imageFile, exifTags);
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

    public boolean isCached(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

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

    public void deleteCachedExifTags(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        File cacheFile = getExifTagsCacheFile(imageFile);

        if (cacheFile.isFile()) {
            LOGGER.log(Level.FINEST, "EXIF Cache: Deleting EXIF cache file ''{0}'' of image file ''{1}''",
                    new Object[]{cacheFile, imageFile});
            cacheFile.delete();
        }
    }

    public void renameCachedExifTags(File oldImageFile, File newImageFile) {
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
    public int clear() {
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
                LOGGER.log(Level.WARNING, "EXIF Cache: Couldn't delete cache file ''{0}''", cacheFile);
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
    }

    private File lookupCacheDirectory() {
        CacheDirectoryProvider provider = Lookup.getDefault().lookup(CacheDirectoryProvider.class);
        File cacheDirectory = provider.getCacheDirectory();
        String cacheDirectoryPath = cacheDirectory.getAbsolutePath();

        return new File(cacheDirectoryPath + File.separator + "ExifCache");
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
}
