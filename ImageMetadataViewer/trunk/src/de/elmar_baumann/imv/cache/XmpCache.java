package de.elmar_baumann.imv.cache;

import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import java.io.File;
import javax.swing.SwingUtilities;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class XmpCache extends Cache<XmpCacheIndirection> {

    private static class XmpFetcher implements Runnable {

        private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        private WorkQueue wq;
        private XmpCache cache;

        XmpFetcher(WorkQueue _wq, XmpCache _cache) {
            wq = _wq;
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
                Xmp xmp = db.getXmpOfFile(file.getAbsolutePath());
                cache.update(xmp, file);
            }
        }
    }

    public XmpCache(ThumbnailsPanel _panel) {
        super(_panel);
        new Thread(new XmpFetcher(workQueue, this), "XmpFetcher").start();
    }

    /**
     * Interface for producers.
     */
    /**
     * Creates a new entry in the cache with the two keys index and filename.
     *
     * Requests for Xmp objects are put into their respective work queues
     * @param file
     * @param prefetch
     */
    @Override
    public synchronized void generateEntry(File file, boolean prefetch) {
        XmpCacheIndirection ci = new XmpCacheIndirection(file);
        updateUsageTime(ci);
        fileCache.put(file, ci);
        if (prefetch) {
            workQueue.append(file);
        } else {
            workQueue.push(file);
        }
    }

    public synchronized void update(Xmp xmp, final File file) {
        if (!fileCache.containsKey(file)) {
            return;  // stale entry
        }
        XmpCacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);
        ci.xmp = xmp;
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
    public synchronized Xmp getXmp(int index) {
        return getXmp(files.get(index));
    }

    public synchronized Xmp getXmp(File file) {
        if (!fileCache.containsKey(file)) {
            generateEntry(file, false);
            return null;
        }
        XmpCacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);

        if (ci.xmp == null) {
            workQueue.push(file);
            return null;
        }
        return ci.xmp;
    }
}
