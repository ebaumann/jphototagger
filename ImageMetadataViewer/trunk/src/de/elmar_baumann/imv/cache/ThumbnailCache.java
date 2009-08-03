package de.elmar_baumann.imv.cache;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class ThumbnailCache {

    private ThumbnailsPanel panel;
    /* carefull, don't set too high.  Although hard memory overruns will be
     * prevented by the SoftReferences, I have seen
     * "java.lang.OutOfMemoryError: GC overhead limit exceeded" things with
     * values of 3500.
     */
    private final int MAX_ENTRIES = 1500;
    private Image dummyThumbnail = IconUtil.getIconImage(
            Bundle.getString("ThumbnailCache.Path.DummyThumbnail"));
    private Image dummyThumbnailScaled = null;
    private Image noPreviewThumbnail = IconUtil.getIconImage(
            Bundle.getString("ThumbnailCache.Path.NoPreviewThumbnail"));

    private WorkQueue imageWQ = new WorkQueue();
    public static int currentAge = 0;

    private class ThumbnailCacheIndirection extends CacheIndirection {
        public Image thumbnail;
        public Image scaled;

        public ThumbnailCacheIndirection(File _file) {
            super(_file);
            thumbnail = null;
            scaled = null;
        }

        @Override
        public boolean isEmpty() {
            return thumbnail == null;
        }
    }

    /**
     * Mapping from file to all kinds of cached data
     */
    private final SoftCacheMap<ThumbnailCacheIndirection> fileCache =
            new SoftCacheMap<ThumbnailCacheIndirection>(MAX_ENTRIES, imageWQ);
    /**
     * Mapping from index to filename
     */
    private List<File> files = new ArrayList<File>();

    private static class ThumbnailFetcher implements Runnable {

        private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        private WorkQueue wq;
        private final ThumbnailCache cache;

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
                    if (image == null) {  // no image available from db
                        image = cache.noPreviewThumbnail;
                    }
                    cache.updateThumbnail(image, file);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public ThumbnailCache(ThumbnailsPanel _panel) {
        panel = _panel;

        ThumbnailFetcher tf = new ThumbnailFetcher(imageWQ, this);
        new Thread(tf, "ThumbnailFetcher1").start();
    }

    /**
     * Interface for producers.
     */
    /**
     * Creates a new entry in the cache with the two keys index and filename.
     *
     * Requests for the real image are put into their
     * respective work queues
     * @param file
     * @param prefetch
     */
    public synchronized void generateEntry(File file, boolean prefetch) {
        ThumbnailCacheIndirection ci = new ThumbnailCacheIndirection(file);
        updateUsageTime(ci);
        fileCache.put(file, ci);
        if (prefetch) {
            imageWQ.append(file);
        } else {
            imageWQ.push(file);
        }
    }

    public synchronized void removeEntry(int index) {
        if (index < 0 || index >= files.size()) {
            return;
        }
        fileCache.remove(files.get(index));
    }

    public synchronized void updateThumbnail(Image image, final File file) {
        if (! fileCache.containsKey(file)) {
            return;  // stale entry
        }
        ThumbnailCacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);
        ci.thumbnail = image;
        ci.scaled = null;
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

    public synchronized Image getThumbnail(int index) {
        return getThumbnail(files.get(index));
    }

    public synchronized Image getThumbnail(File file) {
        if (! fileCache.containsKey(file)) {
            generateEntry(file, false);
        }
        ThumbnailCacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);

        if (ci.thumbnail == null) {
            return dummyThumbnail;
        }
        return ci.thumbnail;
    }

    public void prefetchThumbnails(int indexLow, int indexHigh) {
        for (int i = indexLow; i <= indexHigh; i++) {
            prefetchThumbnail(i);
        }
    }

    public synchronized void prefetchThumbnail(int index) {
        if (index < 0 || index >= files.size()) {
            return;
        }
        prefetchThumbnail(files.get(index));
    }

    public synchronized void prefetchThumbnail(File file) {
        if (fileCache.containsKey(file)) {
            return;  // we have this already
        }
        generateEntry(file, true);
    }

    public synchronized Image getScaledThumbnail(int index, int length) {
        return getScaledThumbnail(files.get(index), length);
    }

    public synchronized Image getScaledThumbnail(File file, int length) {
        boolean generated = false;
        if (! fileCache.containsKey(file)) {
            generateEntry(file, false);
            generated = true;
        }
        ThumbnailCacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);

        if (ci.thumbnail == null) {
            if (! generated) {
                imageWQ.push(file);  // push to front again, is current
            }
            if (dummyThumbnailScaled == null ||
                ! correctlyScaled(dummyThumbnailScaled, length)) {
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

    private void updateUsageTime(CacheIndirection ci) {
        ci.usageTime = currentAge++;
    }
}
