package de.elmar_baumann.imv.cache;

import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public abstract class Cache<CI extends CacheIndirection> {
    public static int currentAge = 0;
    protected final int MAX_ENTRIES = 1500;
    /**
     * Mapping from index to filename
     */
    protected List<File> files = new ArrayList<File>();
    protected ThumbnailsPanel panel;
    protected WorkQueue workQueue = new WorkQueue();
    /**
     * Mapping from file to all kinds of cached data
     */
    protected final SoftCacheMap<CI> fileCache =
            new SoftCacheMap<CI>(MAX_ENTRIES, workQueue);

    protected void updateUsageTime(CacheIndirection ci) {
        ci.usageTime = SubjectCache.currentAge++;
    }

    Cache(ThumbnailsPanel _panel) {
        panel = _panel;
    }

    public void prefetch(int indexLow, int indexHigh) {
        for (int i = indexLow; i <= indexHigh; i++) {
            prefetch(i);
        }
    }

    public synchronized void prefetch(int index) {
        if (index < 0 || index >= files.size()) {
            return;
        }
        prefetch(files.get(index));
    }

    public synchronized void prefetch(File file) {
        if (fileCache.containsKey(file)) {
            return;
        }
        generateEntry(file, true);
    }

    /**
     * Provide a new mapping for indices to filenames.
     *
     * @param _files Ordered list of filenames
     */
    public synchronized void setFiles(List<File> _files) {
        files.clear();
        files.addAll(_files);
    }

    public synchronized void updateFiles(int index, File newFile) {
        // fixme: we must also rename the objects content
        File oldFile = files.get(index);
        CI sci = fileCache.remove(oldFile);
        sci.file = newFile;
        fileCache.put(newFile, sci);
        files.set(index, newFile);
    }

    public synchronized void removeEntry(int index) {
        if (index < 0 || index >= files.size()) {
            return;
        }
        fileCache.remove(files.get(index));
    }

    abstract protected void generateEntry(File file, boolean prefetch);
}
