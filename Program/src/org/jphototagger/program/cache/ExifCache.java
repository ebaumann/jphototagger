package org.jphototagger.program.cache;

import org.jphototagger.lib.concurrent.SerialExecutor;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.event.listener.adapter.DatabaseImageFilesListenerAdapter;
import org.jphototagger.program.image.metadata.exif.ExifMetadata;
import org.jphototagger.program.image.metadata.exif.ExifTags;
import org.jphototagger.program.UserSettings;

import java.io.File;

import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.lib.io.FileUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ExifCache extends DatabaseImageFilesListenerAdapter {
    private static final File CACHE_DIR = new File(UserSettings.getDatabaseBasename() + File.separator + "ExifCache");
    private static final Logger LOGGER = Logger.getLogger(ExifCache.class.getName());
    private static final SerialExecutor SERIAL_EXECUTOR = new SerialExecutor(Executors.newCachedThreadPool());

    public static void cacheExifTags(File imageFile, ExifTags exifTags) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (exifTags == null) {
            throw new NullPointerException("exifTags == null");
        }

        File cacheFile = getExifTagsCacheFile(imageFile);

        try {
            LOGGER.log(Level.FINEST, "Caching EXIF of image file ''{0}'' into cache file ''{1}''",
                       new Object[] { imageFile,
                                      cacheFile });
            exifTags.writeToFile(cacheFile);
        } catch (Throwable ex) {
            AppLogger.logSevere(ExifCache.class, ex);
            cacheFile.delete();
        }
    }

    public static void cacheExifTagsIfNotUpToDate(File imageFile, ExifTags exifTags) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (exifTags == null) {
            throw new NullPointerException("exifTags == null");
        }

        if (!containsUpToDateExifTags(imageFile)) {
            LOGGER.log(Level.FINEST, "Updating EXIF cache file of image file ''{0}''", new Object[] { imageFile });
            cacheExifTags(imageFile, exifTags);
        }
    }

    public static boolean containsUpToDateExifTags(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (!containsExifTags(imageFile)) {
            return false;
        }

        File cacheFile = getExifTagsCacheFile(imageFile);
        long timestampImageFile = imageFile.lastModified();
        long timestampCachedFile = cacheFile.lastModified();

        return timestampCachedFile >= timestampImageFile;
    }

    public static boolean containsExifTags(File imageFile) {
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
    public static ExifTags getCachedExifTags(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        File cacheFile = getExifTagsCacheFile(imageFile);

        if (!cacheFile.isFile()) {
            return null;
        }

        try {
            LOGGER.log(Level.FINEST, "Reading EXIF cache file of image file ''{0}'' from cache file ''{1}''",
                       new Object[] { imageFile,
                                      cacheFile });

            return ExifTags.readFromFile(cacheFile);
        } catch (Throwable ex) {
            AppLogger.logSevere(ExifCache.class, ex);
        }

        return null;
    }

    public static void deleteCachedExifTags(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        File cacheFile = getExifTagsCacheFile(imageFile);

        if (cacheFile.isFile()) {
            LOGGER.log(Level.FINEST, "Deleting EXIF cache file ''{0}'' of image file ''{1}''", new Object[] { cacheFile,
                    imageFile });
            cacheFile.delete();
        }
    }

    public static void renameCachedExifTags(File oldImageFile, File newImageFile) {
        if (!containsExifTags(oldImageFile)) {
            SERIAL_EXECUTOR.execute(new ExifTagsCreator(newImageFile));
        } else {
            File oldCacheFile = getExifTagsCacheFile(oldImageFile);
            File newCacheFile = getExifTagsCacheFile(newImageFile);

            LOGGER.log(
                Level.FINEST,
                "Renaming EXIF cache file ''{0}'' of renamed image file ''{1}'' to cache file ''{2}'' of new image file ''{3}''",
                new Object[] { oldCacheFile, oldImageFile, newCacheFile, newImageFile });

            if (newCacheFile.isFile()) {
                newCacheFile.delete();
            }

            oldCacheFile.renameTo(newCacheFile);
        }
    }

    /**
     * Returns EXIF tags of an image file from the cache if up to date. If the
     * tags are not up to date, they will be created from the image file and cached.
     *
     * @param  imageFile image file
     * @return           tags or null if the tags neither in the cache nor could be
     *                   created from the image file
     */
    public static ExifTags getExifTags(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (containsUpToDateExifTags(imageFile)) {
            return getCachedExifTags(imageFile);
        } else {
            ExifTags exifTags = ExifMetadata.getExifTags(imageFile);

            if (exifTags != null) {
                cacheExifTags(imageFile, exifTags);
            }

            return exifTags;
        }
    }

    public static void ensureCacheDiretoryExists() {
        if (!CACHE_DIR.isDirectory()) {
            try {
                FileUtil.ensureDirectoryExists(CACHE_DIR);
            } catch (Throwable ex) {
                AppLogger.logSevere(ExifCache.class, ex);
            }
        }
    }

    private static File getExifTagsCacheFile(File imageFile) {
        return new File(CACHE_DIR + File.separator + CacheFileUtil.getMd5Filename(imageFile) + ".xml");
    }

    private static class ExifTagsCreator extends Thread {
        private final File imageFile;

        ExifTagsCreator(File imageFile) {
            super("JPhotoTagger: Creating EXIF cache");
            this.imageFile = imageFile;
        }

        @Override
        public void run() {
            LOGGER.log(Level.FINEST, "Reading EXIF of image file ''{0}'' and creating EXIF cache", imageFile);

            ExifTags exifTags = ExifMetadata.getExifTags(imageFile);

            if (exifTags != null) {
                cacheExifTags(imageFile, exifTags);
            }
        }
    }

    @Override
    public void imageFileInserted(File imageFile) {
        SERIAL_EXECUTOR.execute(new ExifTagsCreator(imageFile));
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {
        renameCachedExifTags(oldImageFile, newImageFile);
    }

    @Override
    public void imageFileDeleted(File imageFile) {
        deleteCachedExifTags(imageFile);
    }

    private ExifCache() {}
}
