package de.elmar_baumann.imv.cache;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import java.io.File;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class SubjectCache extends Cache<SubjectCacheIndirection> {

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
        super(_panel);
        new Thread(new SubjectFetcher(workQueue, this),
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
    @Override
    public synchronized void generateEntry(File file, boolean prefetch) {
        SubjectCacheIndirection ci = new SubjectCacheIndirection(file);
        updateUsageTime(ci);
        fileCache.put(file, ci);
        if (prefetch) {
            workQueue.append(file);
        } else {
            workQueue.push(file);
        }
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
            workQueue.push(file);
            return null;
        }
        return ci.subjects;
    }
}
