package org.jphototagger.program.cache;

import org.jphototagger.lib.generics.Pair;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;
import org.jphototagger.program.event.listener.ThumbnailUpdateListener;
import org.jphototagger.program.event.ThumbnailUpdateEvent;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 * @author Martin Pohlack
 */
public final class XmpCache extends Cache<XmpCacheIndirection> implements DatabaseImageFilesListener {
    public static final XmpCache INSTANCE = new XmpCache();
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

    private XmpCache() {
        db.addListener(this);
        new Thread(new XmpFetcher(workQueue, this), "JPhotoTagger: XmpFetcher").start();
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        // special case, directly use new xmp in cache
        update(xmp, imageFile, true);
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        update(imageFile);
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (oldXmp == null) {
            throw new NullPointerException("oldXmp == null");
        }

        if (updatedXmp == null) {
            throw new NullPointerException("updatedXmp == null");
        }

        // special case, directly use new xmp in cache
        update(updatedXmp, imageFile, true);
    }

    @Override
    public void imageFileDeleted(File imageFile) {

        // ignore
    }

    @Override
    public void imageFileInserted(File imageFile) {

        // ignore
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {

        // ignore
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {

        // ignore
    }

    @Override
    public void thumbnailUpdated(File imageFile) {

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
    public void exifInserted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {

        // ignore
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
                        } catch (Exception ex) {}
                    }

                    List<Pair<File, Xmp>> res = db.getXmpOfImageFiles(imageFiles);

                    // send updates to request results
                    for (Pair<File, Xmp> p : res) {
                        File temp = p.getFirst();

                        cache.update(p.getSecond(), temp, true);
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
