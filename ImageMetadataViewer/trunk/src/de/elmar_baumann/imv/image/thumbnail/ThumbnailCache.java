package de.elmar_baumann.imv.image.thumbnail;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class ThumbnailCache {

    private ThumbnailsPanel panel;
    private final int MAX_ENTRIES = 2000;
    private Image dummyThumbnail = ThumbnailUtil.loadImage(
            new File(getClass().getResource(
            Bundle.getString("ThumbnailCache.Path.DummyThumbnail")).getPath()));
    private Image dummyThumbnailScaled = null;

    private void maybeCleanupCache() {
        if (fileCache.size() <= MAX_ENTRIES) {
            return;
        }
        List<CacheIndirection> removeItems =
                new ArrayList<CacheIndirection>(fileCache.values());
        Collections.sort(removeItems, new CacheIndirectionAgeComparator());
        removeItems = removeItems.subList(0, MAX_ENTRIES / 10);
        for (CacheIndirection item : removeItems) {
            fileCache.remove(item.file);
        }
    }

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
            if (!queue.contains(file)) {
                queue.add(file);    // append at end
            }
            notify();
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
    }
    private WorkQueue imageWQ = new WorkQueue();
    private WorkQueue subjectWQ = new WorkQueue();
    public static int currentAge = 0;

    private class CacheIndirection {

        public int usageTime;
        public File file;
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
            implements Comparator<CacheIndirection> {

        @Override
        public int compare(CacheIndirection o1, CacheIndirection o2) {
            return (o1.usageTime < o2.usageTime
                    ? -1
                    : (o1.usageTime == o2.usageTime
                       ? 0
                       : 1));
        }
    }
    /**
     * Mapping from file to all kinds of cached data
     */
    private final Map<File, CacheIndirection> fileCache =
            new HashMap<File, CacheIndirection>();
    /**
     * Mapping from index to filename
     */
    private List<File> files = new ArrayList<File>();

    private static class ThumbnailFetcher implements Runnable {

        private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        private WorkQueue wq;
        private ThumbnailCache cache;

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
        // fixme: we may want to have threadpools here later on.
        Thread sft = new Thread(new SubjectFetcher(subjectWQ, this),
                "SubjectFetcher");
        sft.start();
        Thread tft = new Thread(new ThumbnailFetcher(imageWQ, this),
                "ThumbnailFetcher");
        tft.start();
    }

    /**
     * Interface for producers.
     */
    /**
     * Creates a new entry in the cache with the two keys index and filename.
     *
     * Requests for the real image and the subjects are put into their
     * respective work queues
     */
    public synchronized void generateEntry(File file, boolean wqsubjects) {
        CacheIndirection ci = new CacheIndirection(file);
        updateUsageTime(ci);
        fileCache.put(file, ci);
        imageWQ.push(file);
        if (wqsubjects) {
            subjectWQ.push(file);
        }
    }

    public synchronized void removeEntry(int index) {
        if (index < 0 || index >= files.size()) {
            return;
        }
        fileCache.remove(files.get(index));
    }

    public synchronized void updateThumbnail(Image image, final File file) {
        if (!fileCache.containsKey(file)) {
            return;  // stale entry
        }
        CacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);
        ci.thumbnail = image;
        ci.scaled = null;
        maybeCleanupCache();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                panel.repaint();
            }
        });
    }

    public synchronized void updateSubjects(List<String> subjects, File file) {
        if (!fileCache.containsKey(file)) {
            return;  // stale entry
        }
        CacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);
        ci.subjects = subjects;
        maybeCleanupCache();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                panel.repaint();
            }
        });
    }

    /**
     * Interface for consumers.
     */
    /**
     * Provide a new mapping for indices to filenames.
     *
     * @param names Ordered list of filenames
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
        if (!fileCache.containsKey(file)) {
            generateEntry(file, false);
        }
        CacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);

        if (ci.thumbnail == null) {
            return dummyThumbnail;
        }
        return ci.thumbnail;
    }

    public Image getScaledThumbnail(int index, int length) {
        return getScaledThumbnail(files.get(index), length);
    }

    public Image getScaledThumbnail(File file, int length) {
        if (!fileCache.containsKey(file)) {
            generateEntry(file, false);
        }
        CacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);

        if (ci.thumbnail == null) {
            if (dummyThumbnailScaled == null ||
                    !correctlyScaled(dummyThumbnailScaled, length)) {
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
            generateEntry(file, true);
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
