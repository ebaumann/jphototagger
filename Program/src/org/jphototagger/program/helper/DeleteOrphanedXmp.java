/*
 * @(#)DeleteOrphanedXmp.java    Created on 2008-10-05
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.helper;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.resource.JptBundle;

import java.text.MessageFormat;


/**
 * Löscht in der Datenbank Datensätze mit Dateien, die nicht mehr existieren.
 *
 * @author  Elmar Baumann
 * @see     DatabaseImageFiles#deleteOrphanedXmp(ProgressListener)
 */
public final class DeleteOrphanedXmp implements Runnable, ProgressListener {
    private final ProgressListenerSupport ls = new ProgressListenerSupport();
    private volatile boolean              notifyProgressEnded;
    private volatile boolean              cancel;
    private volatile int                  countDeleted = 0;
    private String                        startMessage;
    private String                        endMessage;

    @Override
    public void run() {
        setMessagesFiles();
        logDeleteRecords();

        DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

        db.deleteNotExistingImageFiles(this);

        if (!cancel) {
            setMessagesXmp();
            notifyProgressEnded = true;    // called before last action
            db.deleteOrphanedXmp(this);
        }
    }

    public int getCountDeleted() {
        return countDeleted;
    }

    /**
     * Fügt einen Fortschrittsbeobachter hinzu. Delegiert an diesen Aufrufe
     * von Database.deleteNotExistingImageFiles().
     *
     * @param listener Fortschrittsbeobachter
     */
    public synchronized void addProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        evt.setInfo(getStartMessage(evt));

        // Catching cancellation request
        for (ProgressListener listener : ls.get()) {
            listener.progressStarted(evt);

            if (evt.isCancel()) {

                // cancel = evt.isCancel() can be wrong when more than 1
                // listener
                cancel = true;
            }
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        // Catching cancellation request
        for (ProgressListener listener : ls.get()) {
            listener.progressPerformed(evt);

            if (evt.isCancel()) {

                // cancel = evt.isCancel() can be wrong when more than
                // 1 listener
                cancel = true;
            }
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        countDeleted += (Integer) evt.getInfo();
        evt.setInfo(getEndMessage());

        if (cancel || notifyProgressEnded) {
            ls.notifyEnded(evt);
        }
    }

    private Object getStartMessage(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return new MessageFormat(startMessage).format(new Object[] {
            evt.getMaximum() });
    }

    private Object getEndMessage() {
        return new MessageFormat(endMessage).format(new Object[] {
            countDeleted });
    }

    private void logDeleteRecords() {
        AppLogger.logInfo(DeleteOrphanedXmp.class,
                          "DeleteOrphanedXmp.Info.StartRemove");
    }

    private void setMessagesFiles() {
        startMessage =
            JptBundle.INSTANCE.getString("DeleteOrphanedXmp.Files.Start");
        endMessage =
            JptBundle.INSTANCE.getString("DeleteOrphanedXmp.Files.End");
    }

    private void setMessagesXmp() {
        startMessage =
            JptBundle.INSTANCE.getString("DeleteOrphanedXmp.Xmp.Start");
        endMessage = JptBundle.INSTANCE.getString("DeleteOrphanedXmp.Xmp.End");
    }
}
