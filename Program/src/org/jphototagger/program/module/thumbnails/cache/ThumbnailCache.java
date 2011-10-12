package org.jphototagger.program.module.thumbnails.cache;

import java.awt.Image;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.jphototagger.domain.event.listener.ThumbnailUpdateListener;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileDeletedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileInsertedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileMovedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailUpdatedEvent;
import org.jphototagger.domain.thumbnails.event.TypedThumbnailUpdateEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;

/**
 *
 * @author Martin Pohlack
 */
public final class ThumbnailCache extends Cache<ThumbnailCacheIndirection> {

    public static final ThumbnailCache INSTANCE = new ThumbnailCache();
    private Image noPreviewThumbnail = IconUtil.getIconImage(Bundle.getString(ThumbnailCache.class, "ThumbnailCache.Path.NoPreviewThumbnail"));
    private static final Logger LOGGER = Logger.getLogger(ThumbnailCache.class.getName());

    private ThumbnailCache() {
        listen();
        ThumbnailFetcher thumbnailFetcher = new ThumbnailFetcher(workQueue, this);
        Thread thumbnailFetcherThread = new Thread(thumbnailFetcher, "JPhotoTagger: ThumbnailFetcher");
        thumbnailFetcherThread.start();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ImageFileDeletedEvent.class)
    public void imageFileDeleted(ImageFileDeletedEvent evt) {
        fileCache.remove(evt.getImageFile());
    }

    @EventSubscriber(eventClass = ImageFileInsertedEvent.class)
    public void imageFileInserted(ImageFileInsertedEvent evt) {
        notifyUpdate(evt.getImageFile());
    }

    @EventSubscriber(eventClass = ImageFileMovedEvent.class)
    public void imageFileRenamed(ImageFileMovedEvent evt) {
        fileCache.remove(evt.getOldImageFile());
        notifyUpdate(evt.getNewImageFile());
    }

    @EventSubscriber(eventClass = ThumbnailUpdatedEvent.class)
    public void thumbnailUpdated(ThumbnailUpdatedEvent evt) {
        File imageFile = evt.getImageFile();
        fileCache.remove(imageFile);
        notifyUpdate(imageFile);
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
            l.thumbnailUpdated(new TypedThumbnailUpdateEvent(file, TypedThumbnailUpdateEvent.Type.THUMBNAIL_UPDATE));
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
                            LOGGER.log(Level.WARNING, "Can''t resolve thumnbail name for image file ''{0}''", imageFile);
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
