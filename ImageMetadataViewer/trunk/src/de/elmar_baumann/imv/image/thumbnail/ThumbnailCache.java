package de.elmar_baumann.imv.image.thumbnail;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.TreeSet;
import javax.swing.SwingUtilities;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class ThumbnailCache {

    private ThumbnailsPanel panel;
    /* carefull, don't set too high.  Although hard memory overruns will be
     * prevented by the SoftReferences, I have seen
     * "java.lang.OutOfMemoryError: GC overhead limit exceeded" things with
     * values of 3500.
     */
    private final int MAX_ENTRIES = 1500;
    private Image dummyThumbnail = IconUtil.getIconImage(
            Bundle.getString("ThumbnailCache.Path.DummyThumbnail"));
    private Image dummyThumbnailScaled = null;
    private Image noPreviewThumbnail = IconUtil.getIconImage(
            Bundle.getString("ThumbnailCache.Path.NoPreviewThumbnail"));

    // based on http://www.exampledepot.com/egs/java.lang/WorkQueue.html
    private class WorkQueue {
        // fixme: maybe use better data structure here with efficient contains()

        Deque<File> queue = new ArrayDeque<File>();

        /**
         * Add a new import work item to head of list
         * 
         * If the item was already in the queue, move to head.
         * 
         * @param file
         */
        public synchronized void push(File file) {
            queue.remove(file);  // maybe remove ...
            queue.push(file);    // and insert at head again
            notify();
        }

        /**
         * Add a new low-priority item at end of list (useful for prefetching).
         *
         * If the item was already in the queue, do nothing.
         *
         * @param file
         */
        public synchronized void append(File file) {
            if (! queue.contains(file)) {
                queue.add(file);    // append at end
                notify();
            }
        }

        /**
         * Retrieve the next item to work on from head of queue.
         *
         * @return File to open next.
         * @throws InterruptedException
         */
        public synchronized File fetch() throws InterruptedException {
            while (queue.isEmpty()) {
                wait();
            }
            return queue.removeFirst();
        }

        /**
         * Remove an image from a queue.
         *
         * @param file
         */
        public synchronized void remove(File file) {
            queue.remove(file);
        }
    }
    private WorkQueue imageWQ = new WorkQueue();
    private WorkQueue subjectWQ = new WorkQueue();
    public static int currentAge = 0;

    private class CacheIndirection {

        public int usageTime;
        final public File file;
        public Image thumbnail;
        public Image scaled;
        public List<String> subjects;

        public CacheIndirection(File _file) {
            file = _file;
            thumbnail = null;
            scaled = null;
            subjects = null;
        }
    }

    private class CacheIndirectionAgeComparator
            implements Comparator<Entry<File, SoftReference<CacheIndirection>>> {

        @Override
        public int compare(Entry<File, SoftReference<CacheIndirection>> o1,
                           Entry<File, SoftReference<CacheIndirection>> o2) {
            CacheIndirection c;
            int t1, t2;

            c = o1.getValue().get();
            if (c == null) {
                t1 = 0;
            } else {
                t1 = o1.getValue().get().usageTime;
            }

            c = o2.getValue().get();
            if (c == null) {
                t2 = 0;
            } else {
                t2 = o2.getValue().get().usageTime;
            }

            return (t1 < t2 ? -1 : (t1 == t2 ? 0 : 1));
        }
    }

    private class SoftCacheMap {
        HashMap<File, SoftReference<CacheIndirection>> _map =
                new HashMap<File, SoftReference<CacheIndirection>>();
        private final int MAX_ENTRIES;

        public SoftCacheMap(int maxEntries) {
            MAX_ENTRIES = maxEntries;
        }

        CacheIndirection get(File k) {
            SoftReference<CacheIndirection> sr = _map.get(k);
            if (sr == null) {
                return null;
            }
            return sr.get();
        }

        CacheIndirection put(File k, CacheIndirection v) {
            SoftReference<CacheIndirection> sr = _map.put(k, new SoftReference<CacheIndirection>(v));
            if (sr == null) {
                return null;
            }
            return sr.get();
        }

        CacheIndirection remove(File k) {
            SoftReference<CacheIndirection> sr = _map.remove(k);
            if (sr == null) {
                return null;
            }
            return sr.get();
        }

        int size() {
            return _map.size();
        }

        boolean containsKey(File k) {
            if (! _map.containsKey(k)) {
                return false;
            }
            return _map.get(k).get() != null;
        }

        public void maybeCleanupCache() {
            /* 1. get EntrySet
             * 2. sort entries according to age in softref, empty softrefs
             *    first, using a sorted set
             * 3. iterate over first n elements and remove them from original
             *    cache
             */
            if (size() <= MAX_ENTRIES) {
                return;
            }

            NavigableSet<Entry<File, SoftReference<CacheIndirection>>> removes =
                    new TreeSet<Entry<File, SoftReference<CacheIndirection>>>
                    (new CacheIndirectionAgeComparator());
            removes.addAll(_map.entrySet());
            Iterator<Entry<File, SoftReference<CacheIndirection>>> it = removes.iterator();

            Entry<File, SoftReference<CacheIndirection>> e;
            CacheIndirection ci;
            for (int index = 0;
                 index < MAX_ENTRIES / 10 && it.hasNext();
                 index++) {
                e = it.next();
                if (e.getValue() == null) {
                    _map.remove(e.getKey());
                    continue;
                }
                ci = e.getValue().get();
                if (ci == null) {
                    _map.remove(e.getKey());
                    continue;
                }
                synchronized(ci) {
                    // check if this image is probably in a prefetch queue and remove it
                    if (ci.thumbnail == null || ci.subjects == null) {
                        imageWQ.remove(ci.file);
                        subjectWQ.remove(ci.file);
                    }
                    _map.remove(ci.file);
                }
            }
        }
    }
    /**
     * Mapping from file to all kinds of cached data
     */
    private final SoftCacheMap fileCache = new SoftCacheMap(MAX_ENTRIES);
    /**
     * Mapping from index to filename
     */
    private List<File> files = new ArrayList<File>();

    private static class ThumbnailFetcher implements Runnable {

        private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        private WorkQueue wq;
        private final ThumbnailCache cache;

        ThumbnailFetcher(WorkQueue imageWQ, ThumbnailCache _cache) {
            wq = imageWQ;
            cache = _cache;
        }

        @Override
        public void run() {
            while (true) {
                File file = null;
                try {
                    file = wq.fetch();
                    Image image = db.getThumbnail(file.getAbsolutePath());
                    if (image == null) {  // no image available from db
                        image = cache.noPreviewThumbnail;
                    }
                    cache.updateThumbnail(image, file);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private static class SubjectFetcher implements Runnable {

        private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        private WorkQueue wq;
        private ThumbnailCache cache;

        SubjectFetcher(WorkQueue subjectWQ, ThumbnailCache _cache) {
            wq = subjectWQ;
            cache = _cache;
        }

        @Override
        public void run() {
            while (true) {
                File file;
                try {
                    file = wq.fetch();
                } catch (InterruptedException e) {
                    continue;
                }
                if (file == null) {
                    continue;
                }
                List<String> subjects = db.getDcSubjectsOfFile(file.
                        getAbsolutePath());
                cache.updateSubjects(subjects, file);
            }
        }
    }

    public ThumbnailCache(ThumbnailsPanel _panel) {
        panel = _panel;
        new Thread(new SubjectFetcher(subjectWQ, this),
                   "SubjectFetcher").start();

        ThumbnailFetcher tf = new ThumbnailFetcher(imageWQ, this);
        new Thread(tf, "ThumbnailFetcher1").start();
        // more threads does not seem to help much, instead it increases the
        // latency a bit for redraw, as newly pushed requests can not overtake
        // active request, which we have more of with many threads.
        /*
        new Thread(tf, "ThumbnailFetcher2").start();
        new Thread(tf, "ThumbnailFetcher3").start();
        new Thread(tf, "ThumbnailFetcher4").start();
         */
    }

    /**
     * Interface for producers.
     */
    /**
     * Creates a new entry in the cache with the two keys index and filename.
     *
     * Requests for the real image and the subjects are put into their
     * respective work queues
     * @param file
     * @param wqsubjects
     * @param prefetch
     */
    public synchronized void generateEntry(File file, boolean wqsubjects,
                                           boolean prefetch) {
        CacheIndirection ci = new CacheIndirection(file);
        updateUsageTime(ci);
        fileCache.put(file, ci);
        if (prefetch) {
            imageWQ.append(file);
            if (wqsubjects) {
                subjectWQ.append(file);
            }
        } else {
            imageWQ.push(file);
            if (wqsubjects) {
                subjectWQ.push(file);
            }
        }
    }

    public synchronized void removeEntry(int index) {
        if (index < 0 || index >= files.size()) {
            return;
        }
        fileCache.remove(files.get(index));
    }

    public synchronized void updateThumbnail(Image image, final File file) {
        if (! fileCache.containsKey(file)) {
            return;  // stale entry
        }
        CacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);
        ci.thumbnail = image;
        ci.scaled = null;
        fileCache.maybeCleanupCache();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                panel.repaint(file);
            }
        });
    }

    public synchronized void updateSubjects(List<String> subjects, final File file) {
        if (!fileCache.containsKey(file)) {
            return;  // stale entry
        }
        CacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);
        ci.subjects = subjects;
        fileCache.maybeCleanupCache();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                panel.repaint(file);
            }
        });
    }

    /**
     * Interface for consumers.
     */
    /**
     * Provide a new mapping for indices to filenames.
     *
     * @param _files Ordered list of filenames
     */
    public void setFiles(List<File> _files) {
        files.clear();
        files.addAll(_files);
    }

    public void updateFiles(int index, File newFile) {
        files.set(index, newFile);
    }

    public synchronized Image getThumbnail(int index) {
        return getThumbnail(files.get(index));
    }

    public synchronized Image getThumbnail(File file) {
        if (! fileCache.containsKey(file)) {
            generateEntry(file, false, false);
        }
        CacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);

        if (ci.thumbnail == null) {
            return dummyThumbnail;
        }
        return ci.thumbnail;
    }

    public void prefetchThumbnails(int indexLow, int indexHigh, boolean subjects) {
        for (int i = indexLow; i <= indexHigh; i++) {
            prefetchThumbnail(i, subjects);
        }
    }

    public synchronized void prefetchThumbnail(int index, boolean subjects) {
        if (index < 0 || index >= files.size()) {
            return;
        }
        prefetchThumbnail(files.get(index), subjects);
    }

    public synchronized void prefetchThumbnail(File file, boolean subjects) {
        if (fileCache.containsKey(file)) {
            return;  // we have this already
        }
        generateEntry(file, subjects, true);
    }

    public synchronized Image getScaledThumbnail(int index, int length) {
        return getScaledThumbnail(files.get(index), length);
    }

    public synchronized Image getScaledThumbnail(File file, int length) {
        boolean generated = false;
        if (! fileCache.containsKey(file)) {
            generateEntry(file, false, false);
            generated = true;
        }
        CacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);

        if (ci.thumbnail == null) {
            if (! generated) {
                imageWQ.push(file);  // push to front again, is current
            }
            if (dummyThumbnailScaled == null ||
                ! correctlyScaled(dummyThumbnailScaled, length)) {
                dummyThumbnailScaled = computeScaled(dummyThumbnail, length);
            }
            return dummyThumbnailScaled;
        } else if (ci.scaled == null) {
            // we have a thumbnail, but no scaled version yet
            ci.scaled = computeScaled(ci.thumbnail, length);
        } else {
            // we have both a thumbnail and a scaled version
            if (!correctlyScaled(ci.scaled, length)) {
                ci.scaled = computeScaled(ci.thumbnail, length);
            }
        }
        return ci.scaled;
    }

    private boolean correctlyScaled(Image image, int length) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        int longer = width > height
                     ? width
                     : height;
        return longer == length;
    }

    private Image computeScaled(Image image, int length) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        double longer = width > height
                        ? width
                        : height;
        if (longer == length) {
            return image;
        }
        double scaleFactor = (double) length / longer;

        int tw = width > height
                 ? length
                 : (int) ((double) width * scaleFactor + 0.5);
        int th = height > width
                 ? length
                 : (int) ((double) height * scaleFactor + 0.5);

        BufferedImage scaled = new BufferedImage(tw, th,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.drawImage(image, 0, 0, tw, th, null);
        g2.dispose();

        return scaled;
    }

    public synchronized List<String> getSubjects(int index) {
        return getSubjects(files.get(index));
    }

    public synchronized List<String> getSubjects(File file) {
        if (!fileCache.containsKey(file)) {
            generateEntry(file, true, false);
            return null;
        }
        CacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);

        if (ci.subjects == null) {
            subjectWQ.push(file);
            return null;
        }
        return ci.subjects;
    }

    private void updateUsageTime(CacheIndirection ci) {
        ci.usageTime = currentAge++;
    }
}
