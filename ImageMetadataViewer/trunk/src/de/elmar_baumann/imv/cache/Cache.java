package de.elmar_baumann.imv.cache;

import de.elmar_baumann.imv.event.listener.ThumbnailUpdateListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public abstract class Cache<CI extends CacheIndirection> {
    
    protected static int currentAge = 0;
    protected final int MAX_ENTRIES = 1500;
    protected final Set<ThumbnailUpdateListener> updateListeners =
            new HashSet<ThumbnailUpdateListener>();
    protected WorkQueue<CI> workQueue = new WorkQueue<CI>();
    /**
     * Mapping from file to all kinds of cached data
     */
    protected final SoftCacheMap<CI> fileCache =
            new SoftCacheMap<CI>(MAX_ENTRIES, workQueue);

    Cache() {}

    protected void updateUsageTime(CacheIndirection ci) {
        ci.usageTime = currentAge++;
    }

    public void addThumbnailUpdateListener(ThumbnailUpdateListener _listener) {
        updateListeners.add(_listener);
    }

    public void removeThumbnailUpdateListener(ThumbnailUpdateListener _listener) {
        updateListeners.remove(_listener);
    }

    public abstract void notifyUpdate(File file);

    public synchronized void prefetch(File file) {
        if (fileCache.containsKey(file)) {
            return;
        }
        generateEntry(file, true);
    }

    public synchronized void updateFiles(File oldFile, File newFile) {
        CI sci = fileCache.remove(oldFile);
        if (sci != null) {
            sci.file = newFile;
            fileCache.put(newFile, sci);
        }
        notifyUpdate(oldFile);
    }

    public synchronized void remove(File file) {
        fileCache.remove(file);
    }

    abstract protected void generateEntry(File file, boolean prefetch);
}
