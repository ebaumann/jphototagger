package de.elmar_baumann.imv.cache;

import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.generics.Pair;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

//        @Override
//        public void run() {
//            while (true) {
//                File file;
//                try {
//                    file = wq.fetch();
//                } catch (InterruptedException e) {
//                    continue;
//                }
//                if (file == null) {
//                    continue;
//                }
//                Xmp xmp = db.getXmpOfFile(file.getAbsolutePath());
//                cache.update(xmp, file);
//            }
//        }
        
        @Override
        public void run() {
            Collection<String> files = new HashSet<String>();
            File file;
            while (true) {
                if (files.size() < 1) {
                    try {
                        file = wq.fetch();
                    } catch (InterruptedException ex) {
                        continue;
                    }
                } else {
                    file = wq.poll();
                }
                if (file != null) {
                    files.add(file.getAbsolutePath());
                }
                assert ! (file == null && files.size() == 0) : "Should not happen";
                if (file == null || files.size() >= 16) {
                    List<Pair<String, Xmp>> res = db.getXmpOfFiles(files);
                    assert files.size() == res.size() : "We should have received all results";
                    for (Pair<String, Xmp> p : res) {
                        cache.update(p.getSecond(), new File(p.getFirst()));
                    }
                    files.clear();
                }
                continue;
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
    protected synchronized void generateEntry(File file, boolean prefetch) {
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
