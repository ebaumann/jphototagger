package org.jphototagger.program.cache;

import java.awt.Image;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.domain.event.ThumbnailUpdateEvent;
import org.jphototagger.domain.event.listener.DatabaseImageFilesListener;
import org.jphototagger.domain.event.listener.ThumbnailUpdateListener;
import org.jphototagger.domain.exif.Exif;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.image.util.IconUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.database.DatabaseImageFiles;

/**
 *
 * @author Martin Pohlack
 */
public final class ThumbnailCache extends Cache<ThumbnailCacheIndirection> implements DatabaseImageFilesListener {

    public static final ThumbnailCache INSTANCE = new ThumbnailCache();
    private Image noPreviewThumbnail = IconUtil.getIconImage(Bundle.getString(ThumbnailCache.class, "ThumbnailCache.Path.NoPreviewThumbnail"));
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private static final Logger LOGGER = Logger.getLogger(ThumbnailCache.class.getName());

    private ThumbnailCache() {
        db.addListener(this);
        new Thread(new ThumbnailFetcher(workQueue, this), "JPhotoTagger: ThumbnailFetcher").start();
    }

    @Override
    public void imageFileDeleted(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        fileCache.remove(imageFile);
    }

    @Override
    public void imageFileInserted(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        notifyUpdate(imageFile);
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {
        if (oldImageFile == null) {
            throw new NullPointerException("oldImageFile == null");
        }

        if (newImageFile == null) {
            throw new NullPointerException("newImageFile == null");
        }

        fileCache.remove(oldImageFile);
        notifyUpdate(newImageFile);
    }

    @Override
    public void thumbnailUpdated(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        fileCache.remove(imageFile);
        notifyUpdate(imageFile);
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {

        // ignore
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {

        // ignore
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {

        // ignore
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {

        // ignore
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {

        // ignore
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {

        // ignore
    }

    @Override
    public void exifInserted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {

        // ignore
    }

    /**
     * Creates a new entry in the cache with the two keys index and file.
     *
     * Requests for the real image are put into their
     * respective work queues
     * @param file
     * @param prefetch
     */
    @Override
    protected synchronized void generateEntry(File file, boolean prefetch) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        ThumbnailCacheIndirection ci = new ThumbnailCacheIndirection(file);

        updateUsageTime(ci);
        fileCache.put(file, ci);

        if (prefetch) {
            workQueue.append(ci);
        } else {
            workQueue.push(ci);
        }
    }

    public synchronized void update(Image image, final File file) {
        if (image == null) {
            throw new NullPointerException("image == null");
        }

        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (!fileCache.containsKey(file)) {
            return;    // stale entry
        }

        ThumbnailCacheIndirection ci = fileCache.get(file);

        updateUsageTime(ci);
        ci.thumbnail = image;
        fileCache.maybeCleanupCache();
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                notifyUpdate(file);
            }
        });
    }

    public synchronized Image getThumbnail(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        ThumbnailCacheIndirection ci;

        while (null == (ci = fileCache.get(file))) {
            generateEntry(file, false);
        }

        updateUsageTime(ci);

        return ci.thumbnail;    // may return zero here if still loading
    }

    @Override
    public void notifyUpdate(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        for (ThumbnailUpdateListener l : updateListeners) {
            l.thumbnailUpdated(new ThumbnailUpdateEvent(file, ThumbnailUpdateEvent.Type.THUMBNAIL_UPDATE));
        }
    }

    private static class ThumbnailFetcher implements Runnable {
        private final ThumbnailCache cache;
        private WorkQueue<ThumbnailCacheIndirection> wq;

        ThumbnailFetcher(WorkQueue<ThumbnailCacheIndirection> imageWQ, ThumbnailCache _cache) {
            wq = imageWQ;
            cache = _cache;
        }

        @Override
        public void run() {
            while (true) {
                File imageFile = null;

                try {
                    imageFile = wq.fetch().file;

                    Image image = null;

                    if (imageFile == null) {
                        LOGGER.log(Level.WARNING, "Didn't find thumbnail of image in preview cache (is null)!");
                    } else {
                        File tnFile = PersistentThumbnails.getThumbnailFile(imageFile);

                        if (tnFile == null) {
                            LOGGER.log(Level.WARNING, "Can't resolve thumnbail name for image file ''{0}''", imageFile);
                        } else {
                            image = PersistentThumbnails.getThumbnail(imageFile);
                        }
                    }

                    if (image == null) {    // no image available from db
                        image = cache.noPreviewThumbnail;
                    }

                    cache.update(image, imageFile);
                } catch (Exception ex) {
                    Logger.getLogger(ThumbnailFetcher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
