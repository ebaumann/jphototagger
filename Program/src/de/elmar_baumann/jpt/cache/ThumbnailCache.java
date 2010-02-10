/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.cache;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.ThumbnailUpdateEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.event.listener.ThumbnailUpdateListener;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.Image;
import java.io.File;
import javax.swing.SwingUtilities;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class ThumbnailCache extends Cache<ThumbnailCacheIndirection>
        implements DatabaseImageFilesListener {

    public static final ThumbnailCache INSTANCE = new ThumbnailCache();
    private Image noPreviewThumbnail = IconUtil.getIconImage(Bundle.getString("ThumbnailCache.Path.NoPreviewThumbnail"));
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

    private ThumbnailCache() {
        db.addListener(this);
        new Thread(new ThumbnailFetcher(workQueue, this),
                "ThumbnailFetcher").start();
    }

    @Override
    public void actionPerformed(DatabaseImageFilesEvent event) {
        switch (event.getType()) {
            case THUMBNAIL_UPDATED:   // fall through
            case IMAGEFILE_DELETED:   // fall through
            case IMAGEFILE_INSERTED:  // fall through
            case IMAGEFILE_UPDATED:
                File file = event.getImageFile().getFile();
                fileCache.remove(file);
                notifyUpdate(file);
                break;
        }
    }

    private static class ThumbnailFetcher implements Runnable {

        private WorkQueue<ThumbnailCacheIndirection> wq;
        private final ThumbnailCache cache;

        ThumbnailFetcher(WorkQueue<ThumbnailCacheIndirection> imageWQ,
                ThumbnailCache _cache) {
            wq = imageWQ;
            cache = _cache;
        }

        @Override
        public void run() {
            while (true) {
                File file = null;
                try {
                    file = wq.fetch().file;
                    Image image = null;
                    if (file == null) {
                        AppLogger.logWarning(ThumbnailFetcher.class, "ThumbnailCache.Info.FileIsNull");
                    } else {
                        String tnFilename = PersistentThumbnails.getMd5Filename(file.getAbsolutePath());
                        if (tnFilename == null) {
                            AppLogger.logWarning(ThumbnailFetcher.class, "ThumbnailCache.Info.NoTnFilename", file);
                        } else {
                            image = PersistentThumbnails.getThumbnail(tnFilename);
                        }
                    }
                    if (image == null) {  // no image available from db
                        image = cache.noPreviewThumbnail;
                    }
                    cache.update(image, file);
                } catch (Exception e) {
                }
            }
        }
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
            workQueue.append(ci);
        } else {
            workQueue.push(ci);
        }
    }

    public synchronized void update(Image image, final File file) {
        if (!fileCache.containsKey(file)) {
            return;  // stale entry
        }
        ThumbnailCacheIndirection ci = fileCache.get(file);
        updateUsageTime(ci);
        ci.thumbnail = image;
        fileCache.maybeCleanupCache();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                notifyUpdate(file);
            }
        });
    }

    public synchronized Image getThumbnail(File file) {
        ThumbnailCacheIndirection ci;
        while (null == (ci = fileCache.get(file))) {
            generateEntry(file, false);
        }
        updateUsageTime(ci);

        return ci.thumbnail;  // may return zero here if still loading
    }

    @Override
    public void notifyUpdate(File file) {
        for (ThumbnailUpdateListener l : updateListeners) {
            l.actionPerformed(new ThumbnailUpdateEvent(file,
                    ThumbnailUpdateEvent.Type.THUMBNAIL_UPDATE));
        }
    }
}
