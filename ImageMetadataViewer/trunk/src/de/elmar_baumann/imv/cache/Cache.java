package de.elmar_baumann.imv.cache;

import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import java.io.File;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public abstract class Cache<CI extends CacheIndirection> {
    
    protected static int currentAge = 0;
    protected final int MAX_ENTRIES = 1500;
    private ThumbnailsPanel panel = null;
    protected WorkQueue workQueue = new WorkQueue();
    /**
     * Mapping from file to all kinds of cached data
     */
    protected final SoftCacheMap<CI> fileCache =
            new SoftCacheMap<CI>(MAX_ENTRIES, workQueue);

    protected void updateUsageTime(CacheIndirection ci) {
        ci.usageTime = currentAge++;
    }

    Cache() {}

    // set target for notifications
    public void setPanel(ThumbnailsPanel _panel) {
        panel = _panel;
    }

    // fixme: we may need a more generic update and register mechanism
    protected void notifyUpdate(File file) {
        if (panel != null) {
            panel.repaint(file);
        }
    }

    public synchronized void prefetch(File file) {
        if (fileCache.containsKey(file)) {
            return;
        }
        generateEntry(file, true);
    }

    public synchronized void updateFiles(File oldFile, File newFile) {
        CI sci = fileCache.remove(oldFile);
        sci.file = newFile;
        fileCache.put(newFile, sci);
    }

    public synchronized void removeEntry(File file) {
        fileCache.remove(file);
    }

    abstract protected void generateEntry(File file, boolean prefetch);
}
