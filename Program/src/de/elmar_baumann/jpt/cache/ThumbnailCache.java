/*
 * @(#)ThumbnailCache.java    Created on 2009-07-18
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

package de.elmar_baumann.jpt.cache;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.event.listener.ThumbnailUpdateListener;
import de.elmar_baumann.jpt.event.ThumbnailUpdateEvent;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.image.util.IconUtil;

import java.awt.Image;

import java.io.File;

import javax.swing.SwingUtilities;

/**
 *
 * @author Martin Pohlack
 */
public final class ThumbnailCache extends Cache<ThumbnailCacheIndirection>
        implements DatabaseImageFilesListener {
    public static final ThumbnailCache INSTANCE           =
        new ThumbnailCache();
    private Image                      noPreviewThumbnail =
        IconUtil.getIconImage(
            JptBundle.INSTANCE.getString(
                "ThumbnailCache.Path.NoPreviewThumbnail"));
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

    private ThumbnailCache() {
        db.addListener(this);
        new Thread(new ThumbnailFetcher(workQueue, this),
                   "ThumbnailFetcher").start();
    }

    @Override
    public void imageFileDeleted(File imageFile) {
        fileCache.remove(imageFile);
    }

    @Override
    public void imageFileInserted(File imageFile) {
        notifyUpdate(imageFile);
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {
        fileCache.remove(oldImageFile);
        notifyUpdate(newImageFile);
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        XmpCache.INSTANCE.xmpUpdated(imageFile, oldXmp, updatedXmp);
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {
        XmpCache.INSTANCE.xmpDeleted(imageFile, xmp);
    }

    @Override
    public void thumbnailUpdated(File imageFile) {
        fileCache.remove(imageFile);
        notifyUpdate(imageFile);
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {

        // ignore
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {

        // ignore
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {

        // ignore
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        XmpCache.INSTANCE.xmpInserted(imageFile, xmp);
    }

    @Override
    public void exifInserted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {

        // ignore
    }

    /**
     * Creates a new entry in the cache with the two keys index and file.
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
            return;    // stale entry
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

        return ci.thumbnail;    // may return zero here if still loading
    }

    @Override
    public void notifyUpdate(File file) {
        for (ThumbnailUpdateListener l : updateListeners) {
            l.actionPerformed(new ThumbnailUpdateEvent(file,
                    ThumbnailUpdateEvent.Type.THUMBNAIL_UPDATE));
        }
    }

    private static class ThumbnailFetcher implements Runnable {
        private final ThumbnailCache                 cache;
        private WorkQueue<ThumbnailCacheIndirection> wq;

        ThumbnailFetcher(WorkQueue<ThumbnailCacheIndirection> imageWQ,
                         ThumbnailCache _cache) {
            wq    = imageWQ;
            cache = _cache;
        }

        @Override
        public void run() {
            while (true) {
                File imageFile = null;

                try {
                    imageFile = wq.fetch().file;

                    Image image = null;

                    if (imageFile == null) {
                        AppLogger.logWarning(ThumbnailFetcher.class,
                                             "ThumbnailCache.Info.FileIsNull");
                    } else {
                        File tnFile =
                            PersistentThumbnails.getThumbnailFileOfImageFile(
                                imageFile);

                        if (tnFile == null) {
                            AppLogger.logWarning(
                                ThumbnailFetcher.class,
                                "ThumbnailCache.Info.NoTnFilename", imageFile);
                        } else {
                            image =
                                PersistentThumbnails.getThumbnailOfImageFile(
                                    imageFile);
                        }
                    }

                    if (image == null) {    // no image available from db
                        image = cache.noPreviewThumbnail;
                    }

                    cache.update(image, imageFile);
                } catch (Exception e) {
                    AppLogger.logSevere(getClass(), e);
                }
            }
        }
    }
}
