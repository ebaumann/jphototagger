package org.jphototagger.xmp;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.event.ImageFileMovedEvent;
import org.jphototagger.domain.event.ImageFileRemovedEvent;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.IoUtil;
import org.jphototagger.lib.util.ServiceLookup;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.services.core.CacheDirectoryProvider;

import com.adobe.xmp.properties.XMPPropertyInfo;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class EmbeddedXmpCache {

    private final File CACHE_DIR;
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
            LOGGER.log(Level.FINE, "Embedded Xmp Cache: Caching embedded XMP of image file ''{0}'' into ''{1}''", new Object[]{imageFile, cacheFile});
            FileUtil.writeStringAsFile(xmpAsString, cacheFile);
            FileUtil.touch(cacheFile, imageFile);
        } catch (Throwable ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
            LOGGER.log(Level.FINEST, "Embedded Xmp Cache: Updating embedded XMP cache file of image file ''{0}''", new Object[]{imageFile});
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

        return timestampCachedFile == timestampImageFile;
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
            LOGGER.log(Level.FINEST, "Embedded Xmp Cache: Reading embedded XMP cache of image file ''{0}'' from cache file ''{1}''",
                    new Object[]{imageFile, cacheFile});
            return FileUtil.getContentAsString(cacheFile, "UTF-8");
        } catch (Throwable throwable) {
            LOGGER.log(Level.SEVERE, null, throwable);
        }

        return null;
    }

    public void deleteCachedXmp(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        File cacheFile = getCacheFile(imageFile);

        if (cacheFile.isFile()) {
            LOGGER.log(Level.FINEST, "Embedded Xmp Cache: Deleting embedded XMP cache file ''{0}'' of image file ''{1}''",
                    new Object[]{cacheFile, imageFile});
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
                "Embedded Xmp Cache: Renaming embedded XMP cache file ''{0}'' of renamed image file ''{1}'' to cache file ''{2}'' of new image file ''{3}''",
                new Object[]{oldCacheFile, oldImageFile, newCacheFile, newImageFile});

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

    private File getCacheFile(File imageFile) {
        return new File(CACHE_DIR + File.separator + FileUtil.getMd5FilenameOfAbsolutePath(imageFile) + ".xml");
    }

    private String getEmptyXmp() {
        InputStream is = null;

        try {
            is = EmbeddedXmpCache.class.getResourceAsStream("Empty.xmp");
            return StringUtil.convertStreamToString(is, "UTF-8");
        } catch (Throwable throwable) {
            LOGGER.log(Level.SEVERE, null, throwable);
        } finally {
            IoUtil.close(is);
        }

        return "";
    }

    @EventSubscriber(eventClass = ImageFileMovedEvent.class)
    public void imageFileMoved(ImageFileMovedEvent event) {
        File oldImageFile = event.getOldImageFile();
        File newImageFile = event.getNewImageFile();

        renameCachedXmp(oldImageFile, newImageFile);
    }

    @EventSubscriber(eventClass = ImageFileRemovedEvent.class)
    public void imageFileRemoved(ImageFileRemovedEvent event) {
        File deletedImageFile = event.getImageFile();

        deleteCachedXmp(deletedImageFile);
    }

    public void init() {
        AnnotationProcessor.process(this);
    }

    private EmbeddedXmpCache() {
        CACHE_DIR = lookupCacheDirectory();
        ensureCacheDiretoryExists();
    }

    private File lookupCacheDirectory() {
        CacheDirectoryProvider provider = ServiceLookup.lookup(CacheDirectoryProvider.class);
        File cacheDirectory = provider.getCacheDirectory();
        String cacheDirectoryPath = cacheDirectory.getAbsolutePath();

        return new File(cacheDirectoryPath + File.separator + "EmbeddedXmpCache");
    }

    private void ensureCacheDiretoryExists() {
        if (!CACHE_DIR.isDirectory()) {
            try {
                LOGGER.log(Level.FINEST, "Embedded Xmp Cache: Creating cache directory ''{0}''", CACHE_DIR);
                FileUtil.ensureDirectoryExists(CACHE_DIR);
            } catch (Throwable ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
}
