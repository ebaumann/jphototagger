package org.jphototagger.program.module.thumbnails.cache;

import java.io.File;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jphototagger.domain.event.listener.ThumbnailUpdateListener;

/**
 *
 * @param <CI>
 * @author Martin Pohlack
 */
public abstract class Cache<CI extends CacheIndirection> {

    private static final int MAX_ENTRIES = 1500;
    private static int currentAge = 0;
    final Set<ThumbnailUpdateListener> updateListeners = new CopyOnWriteArraySet<>();
    protected final WorkQueue<CI> workQueue = new WorkQueue<>();
    /**
     * Mapping from file to all kinds of cached data
     */
    protected final SoftCacheMap<CI> fileCache = new SoftCacheMap<>(MAX_ENTRIES, workQueue);

    Cache() {
    }

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

    public void updateFiles(File oldFile, File newFile) {
        synchronized (this) {
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
