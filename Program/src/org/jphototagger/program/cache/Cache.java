/*
 * @(#)Cache.java    Created on 2009-07-18
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

import org.jphototagger.program.event.listener.ThumbnailUpdateListener;

import java.io.File;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @param <CI>
 * @author Martin Pohlack
 */
public abstract class Cache<CI extends CacheIndirection> {
    static int                         currentAge      = 0;
    private static final int           MAX_ENTRIES     = 1500;
    final Set<ThumbnailUpdateListener> updateListeners =
        new HashSet<ThumbnailUpdateListener>();
    protected WorkQueue<CI> workQueue = new WorkQueue<CI>();

    /**
     * Mapping from file to all kinds of cached data
     */
    protected final SoftCacheMap<CI> fileCache =
        new SoftCacheMap<CI>(MAX_ENTRIES, workQueue);

    Cache() {}

    protected void updateUsageTime(CacheIndirection ci) {
        ci.usageTime = currentAge++;
    }

    public void addThumbnailUpdateListener(ThumbnailUpdateListener _listener) {
        updateListeners.add(_listener);
    }

    public void removeThumbnailUpdateListener(
            ThumbnailUpdateListener _listener) {
        updateListeners.remove(_listener);
    }

    public abstract void notifyUpdate(File file);

    public synchronized void prefetch(File file) {
        if (fileCache.containsKey(file)) {
            return;
        }

        generateEntry(file, true);
    }

    public synchronized void updateFiles(File oldFile, File newFile) {
        CI sci = fileCache.remove(oldFile);

        if (sci != null) {
            sci.file = newFile;
            fileCache.put(newFile, sci);
        }

        notifyUpdate(oldFile);
    }

    public synchronized void remove(File file) {
        fileCache.remove(file);
    }

    abstract protected void generateEntry(File file, boolean prefetch);
}
