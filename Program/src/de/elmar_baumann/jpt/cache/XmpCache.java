/*
 * @(#)XmpCache.java    Created on 2009-07-18
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

import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.event.listener.ThumbnailUpdateListener;
import de.elmar_baumann.jpt.event.ThumbnailUpdateEvent;
import de.elmar_baumann.lib.generics.Pair;

import java.io.File;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 *
 * @author Martin Pohlack
 */
public final class XmpCache extends Cache<XmpCacheIndirection>
        implements DatabaseImageFilesListener {
    public static final XmpCache     INSTANCE = new XmpCache();
    private final DatabaseImageFiles db       = DatabaseImageFiles.INSTANCE;

    private XmpCache() {
        db.addListener(this);
        new Thread(new XmpFetcher(workQueue, this), "XmpFetcher").start();
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        update(imageFile);
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {
        update(imageFile);
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        update(imageFile);
    }

    @Override
    public void imageFileDeleted(File imageFile) {
        // ignore
    }

    @Override
    public void imageFileInserted(File imageFile) {
        // ignore
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {
        // ignore
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {
        // ignore
    }

    @Override
    public void thumbnailUpdated(File imageFile) {
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
    public void exifInserted(File imageFile, Exif exif) {
        // ignore
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {
        // ignore
    }

    private void update(File imageFile) {
        fileCache.remove(imageFile);
        notifyUpdate(imageFile);
    }

    private static class XmpFetcher implements Runnable {
        private final DatabaseImageFiles       db = DatabaseImageFiles.INSTANCE;
        private WorkQueue<XmpCacheIndirection> wq;
        private XmpCache                       cache;

        XmpFetcher(WorkQueue<XmpCacheIndirection> _wq, XmpCache _cache) {
            wq    = _wq;
            cache = _cache;
        }

        @Override
        public void run() {
            Collection<File> imageFiles = new HashSet<File>();
            File             imageFile  = null;

            while (true) {
                if (imageFiles.size() < 1) {
                    try {
                        imageFile = wq.fetch().file;
                    } catch (Exception ex) {
                        continue;
                    }
                } else {
                    XmpCacheIndirection ci = wq.poll();

                    if (ci != null) {
                        imageFile = ci.file;
                    } else {
                        imageFile = null;
                    }
                }

                if (imageFile != null) {
                    imageFiles.add(imageFile);
                }

                assert !((imageFile == null) && (imageFiles.size() == 0)) :
                       "Should not happen";

                if ((imageFile == null) || (imageFiles.size() >= 64)) {
                    if (imageFiles.size() > 1) {
                        try {

                            // wait a bit to allow ThumbnailCache to get some disk bandwidth
                            Thread.sleep(10);
                        } catch (Exception ex) {}
                    }

                    List<Pair<File, Xmp>> res = db.getXmpOfImageFiles(imageFiles);

                    // send updates to request results
                    for (Pair<File, Xmp> p : res) {
                        File temp = p.getFirst();

                        cache.update(p.getSecond(), temp, true);
                        imageFiles.remove(temp);
                    }

                    // if we have files left, there was nothing in the DB, we
                    // fabricate clear xmp objects for them, in order not to
                    // have to ask the DB again
                    for (File f : imageFiles) {
                        Xmp xmp = new Xmp();

                        cache.update(xmp, f, false);
                    }

                    imageFiles.clear();
                }
            }
        }
    }


    /**
     * Interface for producers.
     */

    /**
     * Creates a new entry in the cache with the two keys index and file.
     *
     * Requests for Xmp objects are put into their respective work queues
     * @param file
     * @param prefetch
     */
    @Override
    protected synchronized void generateEntry(File file, boolean prefetch) {
        assert file != null : "Received request with null file";

        XmpCacheIndirection ci = new XmpCacheIndirection(file);

        updateUsageTime(ci);
        fileCache.put(file, ci);

        if (prefetch) {
            workQueue.append(ci);
        } else {
            workQueue.push(ci);
        }
    }

    public synchronized void update(final Xmp xmp, final File file,
                                    boolean repaint) {
        if (!fileCache.containsKey(file)) {
            return;    // stale entry
        }

        XmpCacheIndirection ci = fileCache.get(file);

        updateUsageTime(ci);
        ci.xmp = xmp;
        fileCache.maybeCleanupCache();

        if (repaint) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (xmp.isEmpty()) {
                        notifyUpdate(
                            file, ThumbnailUpdateEvent.Type.XMP_EMPTY_UPDATE);
                    } else {
                        notifyUpdate(file);
                    }
                }
            });
        }
    }

    /**
     * Interface for consumers.
     *
     * @param  file file
     * @return      XMP metadata
     */
    public synchronized Xmp getXmp(File file) {
        XmpCacheIndirection ci = fileCache.get(file);

        if (ci == null) {
            generateEntry(file, false);

            return null;
        }

        updateUsageTime(ci);

        if (ci.xmp == null) {
            workQueue.push(ci);

            return null;
        }

        return ci.xmp;
    }

    public void notifyUpdate(File file, ThumbnailUpdateEvent.Type type) {
        for (ThumbnailUpdateListener l : updateListeners) {
            l.actionPerformed(new ThumbnailUpdateEvent(file, type));
        }
    }

    @Override
    public void notifyUpdate(File file) {
        for (ThumbnailUpdateListener l : updateListeners) {
            l.actionPerformed(new ThumbnailUpdateEvent(file,
                    ThumbnailUpdateEvent.Type.XMP_UPDATE));
        }
    }
}
