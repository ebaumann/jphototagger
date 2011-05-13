package org.jphototagger.program.cache;

import org.jphototagger.program.event.listener.ThumbnailUpdateListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @param <CI>
 * @author Martin Pohlack
 */
public abstract class Cache<CI extends CacheIndirection> {
    static int currentAge = 0;
    private static final int MAX_ENTRIES = 1500;
    final Set<ThumbnailUpdateListener> updateListeners = new HashSet<ThumbnailUpdateListener>();
    protected WorkQueue<CI> workQueue = new WorkQueue<CI>();

    /**
     * Mapping from file to all kinds of cached data
     */
    protected final SoftCacheMap<CI> fileCache = new SoftCacheMap<CI>(MAX_ENTRIES, workQueue);

    Cache() {}

    protected void updateUsageTime(CacheIndirection ci) {
        if (ci == null) {
            throw new NullPointerException("ci == null");
        }

        ci.usageTime = currentAge++;
    }

    public void addThumbnailUpdateListener(ThumbnailUpdateListener _listener) {
        if (_listener == null) {
            throw new NullPointerException("_listener == null");
        }

        updateListeners.add(_listener);
    }

    public void removeThumbnailUpdateListener(ThumbnailUpdateListener _listener) {
        if (_listener == null) {
            throw new NullPointerException("_listener == null");
        }

        updateListeners.remove(_listener);
    }

    public abstract void notifyUpdate(File file);

    public synchronized void prefetch(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (fileCache.containsKey(file)) {
            return;
        }

        generateEntry(file, true);
    }

    public synchronized void updateFiles(File oldFile, File newFile) {
        if (oldFile == null) {
            throw new NullPointerException("oldFile == null");
        }

        if (newFile == null) {
            throw new NullPointerException("newFile == null");
        }

        CI sci = fileCache.remove(oldFile);

        if (sci != null) {
            sci.file = newFile;
            fileCache.put(newFile, sci);
        }

        notifyUpdate(oldFile);
    }

    public synchronized void remove(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        fileCache.remove(file);
    }

    abstract protected void generateEntry(File file, boolean prefetch);
}
