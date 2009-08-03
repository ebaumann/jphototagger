package de.elmar_baumann.imv.cache;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Comparator;
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
public class SubjectCache {

    private ThumbnailsPanel panel;
    private final int MAX_ENTRIES = 1500;
    private WorkQueue subjectWQ = new WorkQueue();
    public static int currentAge = 0;

    private class SubjectCacheIndirection extends CacheIndirection {

        public List<String> subjects;

        public SubjectCacheIndirection(File _file) {
            super(_file);
            subjects = null;
        }

        @Override
        public boolean isEmpty() {
            return subjects == null;
        }
    }

    /**
     * Mapping from file to all kinds of cached data
     */
    private final SoftCacheMap<SubjectCacheIndirection> fileCache =
            new SoftCacheMap<SubjectCacheIndirection>(MAX_ENTRIES, subjectWQ);
    /**
     * Mapping from index to filename
     */
    private List<File> files = new ArrayList<File>();

    private static class SubjectFetcher implements Runnable {

        private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        private WorkQueue wq;
        private SubjectCache cache;

        SubjectFetcher(WorkQueue subjectWQ, SubjectCache _cache) {
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

    public SubjectCache(ThumbnailsPanel _panel) {
        panel = _panel;
        new Thread(new SubjectFetcher(subjectWQ, this),
                   "SubjectFetcher").start();
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
    public synchronized void generateEntry(File file, boolean prefetch) {
        SubjectCacheIndirection ci = new SubjectCacheIndirection(file);
        updateUsageTime(ci);
        fileCache.put(file, ci);
        if (prefetch) {
            subjectWQ.append(file);
        } else {
            subjectWQ.push(file);
        }
    }

    public synchronized void removeEntry(int index) {
        if (index < 0 || index >= files.size()) {
            return;
        }
        fileCache.remove(files.get(index));
    }

    public synchronized void updateSubjects(List<String> subjects, final File file) {
        if (!fileCache.containsKey(file)) {
            return;  // stale entry
        }
        SubjectCacheIndirection ci = fileCache.get(file);
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
        // fixme: we must also rename the objects content
        files.set(index, newFile);
    }

    public void prefetchSubjects(int indexLow, int indexHigh) {
        for (int i = indexLow; i <= indexHigh; i++) {
            prefetchSubjects(i);
        }
    }

    public synchronized void prefetchSubjects(int index) {
        if (index < 0 || index >= files.size()) {
            return;
        }
        prefetchSubjects(files.get(index));
    }

    public synchronized void prefetchSubjects(File file) {
        if (fileCache.containsKey(file)) {
            return;  // we have this already
        }
        generateEntry(file, true);
    }

    public synchronized List<String> getSubjects(int index) {
        return getSubjects(files.get(index));
    }

    public synchronized List<String> getSubjects(File file) {
        if (!fileCache.containsKey(file)) {
            generateEntry(file, false);
            return null;
        }
        SubjectCacheIndirection ci = fileCache.get(file);
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
