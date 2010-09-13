/*
 * @(#)RenderedThumbnailCache.java    Created on 2009-07-18
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.cache;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.event.listener.ThumbnailUpdateListener;
import org.jphototagger.program.event.ThumbnailUpdateEvent;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.renderer.ThumbnailPanelRenderer;
import org.jphototagger.lib.image.util.IconUtil;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;

import java.io.File;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

/**
 * This cache contains scaled and fully rendered thumbnails.  Images can be
 * directly draw by the ThumbnailsPanel, they contain all kinds of markup and
 * overlays.
 *
 * Fixme: refactor common stuff between this and Cache into a common ancestor
 *
 * @author Martin Pohlack
 */
public final class RenderedThumbnailCache implements ThumbnailUpdateListener {
    public static final RenderedThumbnailCache INSTANCE =
        new RenderedThumbnailCache();
    protected final int                        MAX_ENTRIES     = 1500;
    static int                                 currentAge      = 0;
    private final Set<ThumbnailUpdateListener> updateListeners =
        new HashSet<ThumbnailUpdateListener>();
    protected WorkQueue<RenderedThumbnailCacheIndirection> workQueue =
        new WorkQueue<RenderedThumbnailCacheIndirection>();
    private ThumbnailCache thumbCache           = ThumbnailCache.INSTANCE;
    private XmpCache       xmpCache             = XmpCache.INSTANCE;
    private Image          scaledDummyThumbnail = null;
    private Image          dummyThumbnail       =
        IconUtil.getIconImage(
            JptBundle.INSTANCE.getString(
                "RenderedThumbnailCache.Path.DummyThumbnail"));

    /**
     * Mapping from file to all kinds of cached data
     */
    protected final SoftCacheMap<RenderedThumbnailCacheIndirection> fileCache =
        new SoftCacheMap<RenderedThumbnailCacheIndirection>(MAX_ENTRIES,
                         workQueue);
    private ThumbnailPanelRenderer renderer = null;

    private RenderedThumbnailCache() {

        // we need no background thread for the moment, we do all work on
        // request, as the jobs are assumed to be short
        thumbCache.addThumbnailUpdateListener(this);
        XmpCache.INSTANCE.addThumbnailUpdateListener(this);

        Thread t = new Thread(new ThumbnailRenderer(workQueue, this),
                              "JPhotoTagger: ThumbnailRenderer");

        // t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    private synchronized void update(Image image, final File file, int length,
                                     boolean repaint) {
        RenderedThumbnailCacheIndirection ci = fileCache.get(file);

        if (ci == null) {
            return;    // stale entry
        }

        updateUsageTime(ci);
        ci.thumbnail = image;
        ci.length    = length;
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
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        generateEntry(file, renderer.getThumbnailWidth(), false);
    }

    public synchronized void rerenderAll(boolean overlay) {
        int count   = 0,
            skipped = 0;

        for (File file : fileCache.keySet()) {
            count++;

            RenderedThumbnailCacheIndirection ci = fileCache.get(file);

            if (overlay && (ci != null) && ci.renderedForKeywords) {
                skipped++;

                continue;
            }

            if (!overlay && (ci != null) &&!ci.hasKeywords) {
                skipped++;

                continue;
            }

            generateEntry(file, renderer.getThumbnailWidth(), false);
        }
    }

    private static class ThumbnailRenderer implements Runnable {
        private WorkQueue<RenderedThumbnailCacheIndirection> wq;
        private final RenderedThumbnailCache                 cache;

        ThumbnailRenderer(WorkQueue<RenderedThumbnailCacheIndirection> imageWQ,
                          RenderedThumbnailCache _cache) {
            wq    = imageWQ;
            cache = _cache;
        }

        @Override
        public void run() {
            while (true) {
                RenderedThumbnailCacheIndirection rtci = null;

                try {
                    rtci = wq.fetch();
                    assert(rtci.file != null);

                    Image im = cache.thumbCache.getThumbnail(rtci.file);

                    if (im == null) {    // no data available yet
                        if ((cache.scaledDummyThumbnail == null)
                                ||!cache.correctlyScaled(
                                    cache.scaledDummyThumbnail, rtci.length)) {
                            cache.scaledDummyThumbnail =
                                cache.computeScaled(cache.dummyThumbnail,
                                                    rtci.length);
                        }

                        im = cache.scaledDummyThumbnail;
                        im = cache.renderer.getRenderedThumbnail(im, rtci,
                                true);
                    } else {
                        im = cache.computeScaled(im, rtci.length);
                        im = cache.renderer.getRenderedThumbnail(im, rtci,
                                false);
                    }

                    cache.update(im, rtci.file, rtci.length, true);
                } catch (InterruptedException ex) {
                    AppLogger.logSevere(getClass(), ex);
                } catch (RuntimeException ex) {
                    AppLogger.logSevere(getClass(), ex);
                }
            }
        }
    }


    /*
     *  Set renderer object used for constructing the actual images
     */
    public synchronized void setRenderer(ThumbnailPanelRenderer _renderer) {
        if (_renderer == null) {
            throw new NullPointerException("_renderer == null");
        }

        renderer = _renderer;
    }

    public void addThumbnailUpdateListener(ThumbnailUpdateListener _listener) {
        if (_listener == null) {
            throw new NullPointerException("_listener == null");
        }

        updateListeners.add(_listener);
    }

    public void removeThumbnailUpdateListener(
            ThumbnailUpdateListener _listener) {
        if (_listener == null) {
            throw new NullPointerException("_listener == null");
        }

        updateListeners.remove(_listener);
    }

    public void notifyUpdate(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        notifyUpdate(new ThumbnailUpdateEvent(file,
                ThumbnailUpdateEvent.Type.RENDERED_THUMBNAIL_UPDATE));
    }

    public void notifyUpdate(ThumbnailUpdateEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        for (ThumbnailUpdateListener l : updateListeners) {
            l.thumbnailUpdated(evt);
        }
    }

    protected void updateUsageTime(CacheIndirection ci) {
        if (ci == null) {
            throw new NullPointerException("ci == null");
        }

        ci.usageTime = currentAge++;
    }

    public boolean contains(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        return fileCache.containsKey(file);
    }

    /*
     *  Returns a correctly scaled prerendered thumbnail now, or null.
     *
     * If null is return now, every observer will get an update once the
     * newly computed thumbnail is ready.
     */
    public synchronized Image getThumbnail(File file, int length,
            boolean overlay) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        RenderedThumbnailCacheIndirection ci;

        while (null == (ci = fileCache.get(file))) {
            generateEntry(file, length, false);
        }

        updateUsageTime(ci);

        // if null ... request is in flight, update will follow
        // check for correct size and overlay attributes, deliver image if
        // exact match
        if ((ci.thumbnail == null)
                || ((ci.length == length)
                    && (((overlay == false) && (ci.hasKeywords == false))
                        || ((overlay == true)
                            && (ci.renderedForKeywords == true))))) {
            return ci.thumbnail;    // we may return null, or the correct image
        }

        // recreate, we had the wrong size or overlay type
        generateEntry(file, length, false);

        // if we match everything except overlay, return old image for now,
        // but enqueue update request for real update soon, prevents flicker
        // in GUI
        if (ci.length == length) {
            return ci.thumbnail;
        }

        return null;
    }

    protected synchronized void generateEntry(File file, int length,
            boolean prefetch) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

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

    public synchronized void remove(Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("list == null");
        }

        for (File file : files) {
            remove(file);
        }
    }

    public synchronized void remove(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        fileCache.remove(file);
    }

    public synchronized void clear() {
        fileCache.clear();
    }

    private Image computeScaled(Image image, int length) {
        int    width  = image.getWidth(null);
        int    height = image.getHeight(null);
        double longer = (width > height)
                        ? width
                        : height;

        if (longer == length) {
            return image;
        }

        double        scaleFactor = (double) length / longer;
        int           tw          = (width > height)
                                    ? length
                                    : (int) ((double) width * scaleFactor
                                        + 0.5);
        int           th          = (height > width)
                                    ? length
                                    : (int) ((double) height * scaleFactor
                                        + 0.5);
        BufferedImage scaled      = new BufferedImage(tw, th,
                                        BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = scaled.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.drawImage(image, 0, 0, tw, th, null);
        g2.dispose();

        return scaled;
    }

    @Override
    public void thumbnailUpdated(ThumbnailUpdateEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }

        RenderedThumbnailCacheIndirection ci;

        // drop event if we got an empty xmp update for an image without
        // keywords, because it would not change
        if ((event.getType() == ThumbnailUpdateEvent.Type.XMP_EMPTY_UPDATE)
                && (ci = fileCache.get(event.getSource())) != null
                && (ci.hasKeywords == false)) {
            return;
        }

        remove(event.getSource());
        notifyUpdate(event);
    }

    public void updateFiles(File oldFile, File newFile) {
        if (oldFile == null) {
            throw new NullPointerException("oldFile == null");
        }

        if (newFile == null) {
            throw new NullPointerException("newFile == null");
        }

        remove(oldFile);
    }

    public void prefetch(File file, int length, boolean xmp) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

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
        int width  = image.getWidth(null);
        int height = image.getHeight(null);
        int longer = (width > height)
                     ? width
                     : height;

        return longer == length;
    }
}
