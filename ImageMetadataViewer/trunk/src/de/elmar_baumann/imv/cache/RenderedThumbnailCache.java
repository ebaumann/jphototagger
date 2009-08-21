package de.elmar_baumann.imv.cache;

import de.elmar_baumann.imv.event.ThumbnailUpdateEvent;
import de.elmar_baumann.imv.event.listener.ThumbnailUpdateListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.renderer.ThumbnailPanelRenderer;
import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;

/**
 * This cache contains scaled and fully rendered thumbnails.  Images can be
 * directly draw be the ThumbnailsPanel, they contain all kinds of markup and
 * overlays.
 *
 * Fixme: refactor common stuff between this and Cache into a common ancestor
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class RenderedThumbnailCache implements ThumbnailUpdateListener {

    public static final RenderedThumbnailCache INSTANCE =
            new RenderedThumbnailCache();

    protected final int MAX_ENTRIES = 1500;
    protected static int currentAge = 0;
    private final Set<ThumbnailUpdateListener> updateListeners =
            new HashSet<ThumbnailUpdateListener>();
    protected WorkQueue<RenderedThumbnailCacheIndirection> workQueue =
            new WorkQueue<RenderedThumbnailCacheIndirection>();

    private ThumbnailCache thumbCache = ThumbnailCache.INSTANCE;
    private XmpCache xmpCache = XmpCache.INSTANCE;
    private Image scaledDummyThumbnail = null;
    private Image dummyThumbnail = IconUtil.getIconImage(
            Bundle.getString("ThumbnailCache.Path.DummyThumbnail")); // NOI18N
    /**
     * Mapping from file to all kinds of cached data
     */
    protected final SoftCacheMap<RenderedThumbnailCacheIndirection> fileCache =
            new SoftCacheMap<RenderedThumbnailCacheIndirection>(MAX_ENTRIES, null);
    private ThumbnailPanelRenderer renderer = null;

    private RenderedThumbnailCache() {
        // we need no background thread for the moment, we do all work on
        // request, as the jobs are assumed to be short
        thumbCache.addThumbnailUpdateListener(this);
        XmpCache.INSTANCE.addThumbnailUpdateListener(this);
        Thread t = new Thread(new ThumbnailRenderer(workQueue, this),
                "ThumbnailRenderer"); // NOI18N
        //t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    public synchronized void update(Image image, final File file, int length,
            boolean repaint) {
        RenderedThumbnailCacheIndirection ci = fileCache.get(file);
        if (ci == null) {
            return;  // stale entry
        }

        updateUsageTime(ci);
        ci.thumbnail = image;
        ci.length = length;
        fileCache.maybeCleanupCache();
        if (repaint) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    notifyUpdate(file);
                }
            });
        }
    }

    public void rerender(File file) {
        generateEntry(file, renderer.getThumbnailWidth(), false);
    }

    private static class ThumbnailRenderer implements Runnable {

        private WorkQueue<RenderedThumbnailCacheIndirection> wq;
        private final RenderedThumbnailCache cache;

        ThumbnailRenderer(WorkQueue<RenderedThumbnailCacheIndirection> imageWQ,
                RenderedThumbnailCache _cache) {
            wq = imageWQ;
            cache = _cache;
        }

        @Override
        public void run() {
            while (true) {
                RenderedThumbnailCacheIndirection rtci = null;
                try {
                    rtci = wq.fetch();
                    Image im = cache.thumbCache.getThumbnail(rtci.file);
                    if (im == null) {  // no data available yet
                        if (cache.scaledDummyThumbnail == null ||
                                ! cache.correctlyScaled(
                                cache.scaledDummyThumbnail, rtci.length)) {
                            cache.scaledDummyThumbnail =
                                    cache.computeScaled(cache.dummyThumbnail,
                                    rtci.length);
                        }
                        im = cache.scaledDummyThumbnail;
                        im = cache.renderer.getRenderedThumbnail(im, rtci.file,
                                true);
                    } else {
                        im = cache.computeScaled(im, rtci.length);
                        im = cache.renderer.getRenderedThumbnail(im, rtci.file,
                                false);
                    }
                    cache.update(im, rtci.file, rtci.length, true);
                } catch (InterruptedException e) {}
            }
        }
    }

    /* Set renderer object used for constructing the actual images
     */
    public synchronized void setRenderer(ThumbnailPanelRenderer _renderer) {
        renderer = _renderer;
    }

    public void addThumbnailUpdateListener(ThumbnailUpdateListener _listener) {
        updateListeners.add(_listener);
    }

    public void removeThumbnailUpdateListener(ThumbnailUpdateListener _listener) {
        updateListeners.remove(_listener);
    }

    public void notifyUpdate(File file) {
        notifyUpdate(new ThumbnailUpdateEvent(file,
                ThumbnailUpdateEvent.Type.RENDERED_THUMBNAIL_UPDATE));
    }

    public void notifyUpdate(ThumbnailUpdateEvent e) {
        for (ThumbnailUpdateListener l : updateListeners) {
            l.actionPerformed(e);
        }
    }

    protected void updateUsageTime(CacheIndirection ci) {
        ci.usageTime = currentAge++;
    }

    /* Returns a correctly scaled prerendered thumbnail now, or null.
     *
     * If null is return now, every observer will get an update once the
     * newly computed thumbnail is ready.
     */
    public synchronized Image getThumbnail(File file, int length) {
        RenderedThumbnailCacheIndirection ci;
        while (null == (ci = fileCache.get(file))) {
            generateEntry(file, length, false);
        }
        updateUsageTime(ci);

        if (ci.thumbnail == null || ci.length == length) {
            return ci.thumbnail;  // we may return null, or the correct image
        }
        // recreate, we had the wrong size, return null for now
        generateEntry(file, length, false);
        return null;
    }

    protected synchronized void generateEntry(File file, int length,
            boolean prefetch) {
        RenderedThumbnailCacheIndirection ci =
                new RenderedThumbnailCacheIndirection(file, length);
        fileCache.put(file, ci);
        updateUsageTime(ci);
        if (prefetch) {
            workQueue.append(ci);
        } else {
            workQueue.push(ci);
        }
    }

    public synchronized void remove(File file) {
        fileCache.remove(file);
    }

    public synchronized void clear() {
        fileCache.clear();
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

    @Override
    public void actionPerformed(ThumbnailUpdateEvent event) {
        // fixme: we could check whether we are currently displaying
        //        Metadata and only react to those events if we do
        remove(event.getSource());
        notifyUpdate(event);
    }

    public void updateFiles(File oldFile, File newFile) {
        remove(oldFile);
        thumbCache.updateFiles(oldFile, newFile);
    }

    public void prefetch(File file, int length, boolean xmp) {
        if (fileCache.containsKey(file)) {
            return;
        }

        thumbCache.prefetch(file);
        if (xmp) {
            xmpCache.prefetch(file);
        }
        generateEntry(file, length, true);
    }

    private boolean correctlyScaled(Image image, int length) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        int longer = width > height
                     ? width
                     : height;
        return longer == length;
    }
}
