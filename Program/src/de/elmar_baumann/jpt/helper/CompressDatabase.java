/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.database.DatabaseMaintainance;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.event.listener.impl.ProgressListenerSupport;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.types.Filename;
import java.io.File;

/**
 * Compresses the database.
 *
 * @author  Elmar Baumann
 * @version 2008-10-30
 */
public final class CompressDatabase implements Runnable {

    private final ProgressListenerSupport listenerSupport = new ProgressListenerSupport();
    private       boolean                 success         = false;
    private       long                    sizeBefore;
    private       long                    sizeAfter;

    public synchronized void addProgressListener(ProgressListener l) {
        listenerSupport.add(l);
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
        ProgressEvent evt = new ProgressEvent(this, JptBundle.INSTANCE.getString("CompressDatabase.Start"));

        listenerSupport.notifyStarted(evt);
    }

    private void logCompressDatabase() {
        AppLogger.logInfo(CompressDatabase.class, "CompressDatabase.Info.StartCompress");
    }

    private synchronized void notifyEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, 1, 1, getEndMessage());

        listenerSupport.notifyEnded(evt);
    }

    private Object getEndMessage() {
        double mb = 1024 * 1024;
        Object[] params = { success
                ? JptBundle.INSTANCE.getString("CompressDatabase.End.Success.True")
                : JptBundle.INSTANCE.getString("CompressDatabase.End.Success.False"),
            sizeBefore, new Double(sizeBefore / mb), sizeAfter, new Double(sizeAfter / mb)
        };
        return JptBundle.INSTANCE.getString("CompressDatabase.End", params);
    }
}
