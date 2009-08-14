package de.elmar_baumann.imv.cache;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.SwingUtilities;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class ThumbnailCache extends Cache<ThumbnailCacheIndirection> {

    private Image dummyThumbnail = IconUtil.getIconImage(
            Bundle.getString("ThumbnailCache.Path.DummyThumbnail"));
    private Image dummyThumbnailScaled = null;
    private Image noPreviewThumbnail = IconUtil.getIconImage(
            Bundle.getString("ThumbnailCache.Path.NoPreviewThumbnail"));

    private static class ThumbnailFetcher implements Runnable {

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
                    Image image = PersistentThumbnails.getThumbnail(
                            PersistentThumbnails.getMd5File(
                            file.getAbsolutePath()));
                    if (image == null) {  // no image available from db
                        image = cache.noPreviewThumbnail;
                    }
                    cache.update(image, file);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public ThumbnailCache(ThumbnailsPanel _panel) {
        super(_panel);
        new Thread(new ThumbnailFetcher(workQueue, this),
                "ThumbnailFetcher").start(); // NOI18N
    }

    /**
     * Creates a new entry in the cache with the two keys index and filename.
     *
     * Requests for the real image are put into their
     * respective work queues
     * @param file
     * @param prefetch
     */
    @Override
    protected synchronized void generateEntry(File file, boolean prefetch) {
        ThumbnailCacheIndirection ci = new ThumbnailCacheIndirection(file);
        updateUsageTime(ci);
        fileCache.put(file, ci);
        if (prefetch) {
            workQueue.append(file);
        } else {
            workQueue.push(file);
        }
    }

    public synchronized void update(Image image, final File file) {
        if (!fileCache.containsKey(file)) {
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

    public synchronized Image getThumbnail(File file) {
        if (!fileCache.containsKey(file)) {
            generateEntry(file, false);
        }
        ThumbnailCacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);

        if (ci.thumbnail == null) {
            return dummyThumbnail;
        }
        return ci.thumbnail;
    }

    public synchronized Image getScaledThumbnail(File file, int length) {
        boolean generated = false;
        if (!fileCache.containsKey(file)) {
            generateEntry(file, false);
            generated = true;
        }
        ThumbnailCacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);

        if (ci.thumbnail == null) {
            if (!generated) {
                workQueue.push(file);  // push to front again, is current
            }
            if (dummyThumbnailScaled == null ||
                    !correctlyScaled(dummyThumbnailScaled, length)) {
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
}
