package de.elmar_baumann.imv.cache;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.event.ThumbnailUpdateEvent;
import de.elmar_baumann.imv.event.listener.ThumbnailUpdateListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.Image;
import java.io.File;
import javax.swing.SwingUtilities;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class ThumbnailCache extends Cache<ThumbnailCacheIndirection> {

    public static final ThumbnailCache INSTANCE = new ThumbnailCache();
    private Image noPreviewThumbnail = IconUtil.getIconImage(
            Bundle.getString("ThumbnailCache.Path.NoPreviewThumbnail")); // NOI18N

    private ThumbnailCache() {
        new Thread(new ThumbnailFetcher(workQueue, this),
                "ThumbnailFetcher").start(); // NOI18N
    }

    private static class ThumbnailFetcher implements Runnable {

        private WorkQueue<ThumbnailCacheIndirection> wq;
        private final ThumbnailCache cache;

        ThumbnailFetcher(WorkQueue<ThumbnailCacheIndirection> imageWQ,
                ThumbnailCache _cache) {
            wq = imageWQ;
            cache = _cache;
        }

        @Override
        public void run() {
            while (true) {
                File file = null;
                try {
                    file = wq.fetch().file;
                    Image image = null;
                    if (file != null) {
                        String tnFilename = PersistentThumbnails.getMd5File(
                                file.getAbsolutePath());
                        if (tnFilename == null) {
                            AppLog.logWarning(ThumbnailFetcher.class,
                                    "ThumbnailFetcher.Info.NoTnFilename", file);
                        } else {
                            image =
                                    PersistentThumbnails.getThumbnail(tnFilename);
                        }
                    }
                    if (image == null) {  // no image available from db
                        image = cache.noPreviewThumbnail;
                    }
                    cache.update(image, file);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * Creates a new entry in the cache with the two keys index and filename.
     *
     * Requests for the real image are put into their
     * respective work queues
     * @param file
     * @param prefetch
     */
    @Override
    protected synchronized void generateEntry(File file, boolean prefetch) {
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
        if (!fileCache.containsKey(file)) {
            return;  // stale entry
        }
        ThumbnailCacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);
        ci.thumbnail = image;
        fileCache.maybeCleanupCache();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                notifyUpdate(file);
            }
        });
    }

    public synchronized Image getThumbnail(File file) {
        ThumbnailCacheIndirection ci;
        while (null == (ci = fileCache.get(file))) {
            generateEntry(file, false);
        }
        updateUsageTime(ci);

        return ci.thumbnail;  // may return zero here if still loading
    }

    @Override
    public void notifyUpdate(File file) {
        for (ThumbnailUpdateListener l : updateListeners) {
            l.actionPerformed(new ThumbnailUpdateEvent(file,
                    ThumbnailUpdateEvent.Type.THUMBNAIL_UPDATE));
        }
    }
}
