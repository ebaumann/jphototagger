package org.jphototagger.program.cache;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.thumbnails.event.ThumbnailUpdateEvent;
import org.jphototagger.domain.event.listener.ThumbnailUpdateListener;
import org.jphototagger.domain.repository.event.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.XmpUpdatedEvent;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.xmp.FileXmp;

/**
 *
 * @author Martin Pohlack
 */
public final class XmpCache extends Cache<XmpCacheIndirection> {

    public static final XmpCache INSTANCE = new XmpCache();
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

    private XmpCache() {
        AnnotationProcessor.process(this);
        new Thread(new XmpFetcher(workQueue, this), "JPhotoTagger: XmpFetcher").start();
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        // special case, directly use new xmp in cache
        update(evt.getXmp(), evt.getImageFile(), true);
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(XmpDeletedEvent evt) {
        update(evt.getImageFile());
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        // special case, directly use new xmp in cache
        update(evt.getUpdatedXmp(), evt.getImageFile(), true);
    }

    private void update(File imageFile) {
        fileCache.remove(imageFile);
        notifyUpdate(imageFile);
    }

    private static class XmpFetcher implements Runnable {

        private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        private WorkQueue<XmpCacheIndirection> wq;
        private XmpCache cache;

        XmpFetcher(WorkQueue<XmpCacheIndirection> _wq, XmpCache _cache) {
            wq = _wq;
            cache = _cache;
        }

        @Override
        public void run() {
            Collection<File> imageFiles = new HashSet<File>();
            File imageFile = null;

            while (true) {
                if (imageFiles.size() < 1) {
                    try {
                        imageFile = wq.fetch().file;
                    } catch (Exception ex) {
                        continue;
                    }
                } else {
                    XmpCacheIndirection ci = wq.poll();

                    if (ci != null) {
                        imageFile = ci.file;
                    } else {
                        imageFile = null;
                    }
                }

                if (imageFile != null) {
                    imageFiles.add(imageFile);
                }

                assert !((imageFile == null) && (imageFiles.size() == 0)) : "Should not happen";

                if ((imageFile == null) || (imageFiles.size() >= 64)) {
                    if (imageFiles.size() > 1) {
                        try {

                            // wait a bit to allow ThumbnailCache to get some disk bandwidth
                            Thread.sleep(10);
                        } catch (Exception ex) {
                        }
                    }

                    List<FileXmp> res = db.getXmpOfImageFiles(imageFiles);
                    boolean repaint = true;

                    // send updates to request results
                    for (FileXmp fileXmp : res) {
                        File temp = fileXmp.getFile();
                        Xmp xmp = fileXmp.getXmp();

                        cache.update(xmp, temp, repaint);
                        imageFiles.remove(temp);
                    }

                    // if we have files left, there was nothing in the DB, we
                    // fabricate clear xmp objects for them, in order not to
                    // have to ask the DB again
                    for (File f : imageFiles) {
                        Xmp xmp = new Xmp();

                        cache.update(xmp, f, false);
                    }

                    imageFiles.clear();
                }
            }
        }
    }

    /**
     * Interface for producers.
     */
    /**
     * Creates a new entry in the cache with the two keys index and file.
     *
     * Requests for Xmp objects are put into their respective work queues
     * @param file
     * @param prefetch
     */
    @Override
    protected synchronized void generateEntry(File file, boolean prefetch) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        XmpCacheIndirection ci = new XmpCacheIndirection(file);

        updateUsageTime(ci);
        fileCache.put(file, ci);

        if (prefetch) {
            workQueue.append(ci);
        } else {
            workQueue.push(ci);
        }
    }

    public synchronized void update(final Xmp xmp, final File file, boolean repaint) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (!fileCache.containsKey(file)) {
            return;    // stale entry
        }

        XmpCacheIndirection ci = fileCache.get(file);

        updateUsageTime(ci);
        ci.xmp = xmp;
        fileCache.maybeCleanupCache();

        if (repaint) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    if (xmp.isEmpty()) {
                        notifyUpdate(file, ThumbnailUpdateEvent.Type.XMP_EMPTY_UPDATE);
                    } else {
                        notifyUpdate(file);
                    }
                }
            });
        }
    }

    /**
     * Interface for consumers.
     *
     * @param  file file
     * @return      XMP metadata
     */
    public synchronized Xmp getXmp(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        XmpCacheIndirection ci = fileCache.get(file);

        if (ci == null) {
            generateEntry(file, false);

            return null;
        }

        updateUsageTime(ci);

        if (ci.xmp == null) {
            workQueue.push(ci);

            return null;
        }

        return ci.xmp;
    }

    public void notifyUpdate(File file, ThumbnailUpdateEvent.Type type) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (type == null) {
            throw new NullPointerException("type == null");
        }

        for (ThumbnailUpdateListener l : updateListeners) {
            l.thumbnailUpdated(new ThumbnailUpdateEvent(file, type));
        }
    }

    @Override
    public void notifyUpdate(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        for (ThumbnailUpdateListener l : updateListeners) {
            l.thumbnailUpdated(new ThumbnailUpdateEvent(file, ThumbnailUpdateEvent.Type.XMP_UPDATE));
        }
    }
}
