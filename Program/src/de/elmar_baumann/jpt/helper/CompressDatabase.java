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
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.database.DatabaseMaintainance;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.types.Filename;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Compresses the database.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-30
 */
public final class CompressDatabase implements Runnable {

    private final List<ProgressListener> listeners =
            new ArrayList<ProgressListener>();
    private boolean success = false;
    private long sizeBefore;
    private long sizeAfter;

    public synchronized void addProgressListener(ProgressListener l) {
        listeners.add(l);
    }

    /**
     * Returns the success of the database operation.
     * 
     * @return true if successfully compressed.
     */
    public boolean getSuccess() {
        return success;
    }

    /**
     * Returns the size of the database file in bytes before compressing.
     *
     * <em>The value is (only) valid after compressing the database file!</em>
     *
     * @return size in bytes
     */
    public long getSizeAfter() {
        return sizeAfter;
    }

    /**
     * Returns the size of the database file in bytes after compressing.
     *
     * <em>The value is (only) valid after compressing the database file!</em>
     *
     * @return size in bytes
     */
    public long getSizeBefore() {
        return sizeBefore;
    }

    /**
     * Compresses the database. The process can't be stopped and the progress
     * values can't submitted to them. The listeners are only notified about
     * the start and end of compressing.
     */
    @Override
    public void run() {
        logCompressDatabase();
        notifyStarted();
        File dbFile = new File(UserSettings.INSTANCE.getDatabaseFileName(
                Filename.FULL_PATH));
        sizeBefore = dbFile.length();
        success = DatabaseMaintainance.INSTANCE.compressDatabase();
        sizeAfter = dbFile.length();
        notifyEnded();
    }

    private synchronized void notifyStarted() {
        ProgressEvent evt = new ProgressEvent(this,
                Bundle.getString("DatabaseCompress.Start"));
        for (ProgressListener listener : listeners) {
            listener.progressStarted(evt);
        }
    }

    private void logCompressDatabase() {
        AppLog.logInfo(CompressDatabase.class,
                "DatabaseCompress.Info.StartCompress");
    }

    private synchronized void notifyEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, 1, 1, getEndMessage());
        for (ProgressListener listener : listeners) {
            listener.progressEnded(evt);
        }
    }

    private Object getEndMessage() {
        double mb = 1024 * 1024;
        Object[] params = {Bundle.getString(
            success
            ? "DatabaseCompress.End.Success.True"
            : "DatabaseCompress.End.Success.False"),
            sizeBefore, new Double(sizeBefore / mb), sizeAfter,
            new Double(sizeAfter / mb)
        };
        return Bundle.getString("DatabaseCompress.End", params);
    }
}
