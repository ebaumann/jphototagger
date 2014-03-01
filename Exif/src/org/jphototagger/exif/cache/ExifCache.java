package org.jphototagger.exif.cache;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.storage.CacheDirectoryProvider;
import org.jphototagger.domain.metadata.exif.event.ExifCacheClearedEvent;
import org.jphototagger.domain.metadata.exif.event.ExifCacheFileDeletedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileDeletedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileMovedEvent;
import org.jphototagger.exif.ExifTags;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.xml.bind.XmlObjectExporter;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ExifCache {

    public static final ExifCache INSTANCE = new ExifCache();
    private static final Logger LOGGER = Logger.getLogger(ExifCache.class.getName()); // Has to be instanciated before INSTANCE!
    private final File cacheDbFile;
    private final DB cacheDb;
    private final Map<String, String> exifTags;

    public synchronized void cacheExifTags(File imageFile, ExifTags exifTags) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        if (exifTags == null) {
            throw new NullPointerException("exifTags == null");
        }
        LOGGER.log(Level.FINEST, "Caching EXIF metadata of image file ''{0}''", imageFile);
        exifTags.setLastModified(imageFile.lastModified());
        try {
            String exifTagsXml = XmlObjectExporter.marshal(exifTags);
            this.exifTags.put(imageFile.getAbsolutePath(), exifTagsXml);
            cacheDb.commit();
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
    }

    public synchronized boolean containsUpToDateExifTags(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        if (!isCached(imageFile)) {
            return false;
        }
        try {
            String exifTagsXml = exifTags.get(imageFile.getAbsolutePath());
            ExifTags tags = XmlObjectImporter.unmarshal(exifTagsXml, ExifTags.class);
            return tags.getLastModified() == imageFile.lastModified();
        } catch (Throwable t) {
            Logger.getLogger(ExifCache.class.getName()).log(Level.SEVERE, null, t);
            return false;
        }
    }

    private synchronized boolean isCached(File imageFile) {
        return exifTags.containsKey(imageFile.getAbsolutePath());
    }

    public synchronized ExifTags getCachedExifTags(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        try {
            LOGGER.log(Level.FINEST, "Reading cached EXIF metadata of image file ''{0}''", imageFile);
            String exifTagsXml = exifTags.get(imageFile.getAbsolutePath());
            return XmlObjectImporter.unmarshal(exifTagsXml, ExifTags.class);
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
            return null;
        }
    }

    private void deleteCachedExifTags(File imageFile) {
        try {
            synchronized (this) {
                if (exifTags.remove(imageFile.getAbsolutePath()) != null) {
                    cacheDb.commit();
                    LOGGER.log(Level.FINEST, "Deleted cache EXIF metadata of image file ''{0}''", imageFile);
                }
            }
            EventBus.publish(new ExifCacheFileDeletedEvent(this, imageFile));
        } catch (Throwable t) {
            Logger.getLogger(ExifCache.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    private synchronized void renameCachedExifTags(File oldImageFile, File newImageFile) {
        if (isCached(oldImageFile)) {
            try {
                String exifTagsXml = exifTags.get(oldImageFile.getAbsolutePath());
                exifTags.remove(oldImageFile.getAbsolutePath());
                exifTags.put(newImageFile.getAbsolutePath(), exifTagsXml);
                cacheDb.commit();
                LOGGER.log(
                        Level.FINEST,
                        "Renamed image file of cached EXIF metadata from ''{0}'' to ''{1}''",
                        new Object[]{oldImageFile, newImageFile});
            } catch (Throwable t) {
                Logger.getLogger(ExifCache.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    /**
     * @return count of deleted cached EXIF metadata
     */
    int clear() {
        synchronized (this) {
            if (exifTags.isEmpty()) {
                return 0;
            }
        }
        LOGGER.log(Level.INFO, "Deleting all cached EXIF metadata");
        int count;
        try {
            synchronized (this) {
                count = exifTags.size();
                exifTags.clear();
                cacheDb.commit();
            }
            EventBus.publish(new ExifCacheClearedEvent(this, count));
            return count;
        } catch (Throwable t) {
            Logger.getLogger(ExifCache.class.getName()).log(Level.SEVERE, null, t);
            return 0;
        }
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

    void init() {
        AnnotationProcessor.process(this);
    }

    File getCacheDir() {
        return cacheDbFile.getParentFile();
    }

    private ExifCache() {
        cacheDbFile = new File(lookupCacheDirectory().getAbsolutePath() + File.separator + "ExifCacheDb");
        ensureCacheDiretoryExists();
        cacheDb = DBMaker.newFileDB(cacheDbFile)
                .closeOnJvmShutdown()
                .make();
        exifTags = cacheDb.getHashMap("exifcache");
    }

    private File lookupCacheDirectory() {
        CacheDirectoryProvider provider = Lookup.getDefault().lookup(CacheDirectoryProvider.class);
        return provider.getCacheDirectory("ExifCache");
    }

    private void ensureCacheDiretoryExists() {
        File cacheDir = cacheDbFile.getParentFile();
        if (!cacheDir.isDirectory()) {
            try {
                LOGGER.log(Level.FINEST, "Creating cache directory ''{0}''", cacheDir);
                FileUtil.ensureDirectoryExists(cacheDir);
            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE, null, t);
            }
        }
    }
}
