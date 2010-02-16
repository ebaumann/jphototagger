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

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.event.listener.impl.ProgressListenerSupport;
import de.elmar_baumann.jpt.resource.JptBundle;
import java.text.MessageFormat;
import java.util.Set;

/**
 * Löscht in der Datenbank Datensätze mit Dateien, die nicht mehr existieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 * @see     DatabaseImageFiles#deleteOrphanedXmp(de.elmar_baumann.jpt.event.listener.ProgressListener)
 */
public final class DeleteOrphanedXmp
        implements Runnable, ProgressListener {

    private final    ProgressListenerSupport listenerSupport   = new ProgressListenerSupport();
    private volatile boolean                 notifyProgressEnded;
    private volatile boolean                 stop;
    private volatile int                     countDeleted       = 0;
    private          String                  startMessage;
    private          String                  endMessage;

    @Override
    public void run() {
        setMessagesFiles();
        logDeleteRecords();
        DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        db.deleteNotExistingImageFiles(this);
        if (!stop) {
            setMessagesXmp();
            notifyProgressEnded = true; // called before last action
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
        listenerSupport.add(listener);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {

        evt.setInfo(getStartMessage(evt));

        // Getting listeners to catch stop request
        Set<ProgressListener> listeners = listenerSupport.get();

        synchronized (listeners) {
            for (ProgressListener listener : listeners) {
                listener.progressStarted(evt);
                if (evt.isStop()) {
                    stop = true; // stop = evt.isStop() can be wrong when more than 1 listener
                }
            }
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {

        // Getting listeners to catch stop request
        Set<ProgressListener> listeners = listenerSupport.get();

        synchronized (listeners) {
            for (ProgressListener listener : listeners) {
                listener.progressPerformed(evt);
                if (evt.isStop()) {
                    stop = true; // stop = evt.isStop() can be wrong when more than 1 listener
                }
            }
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        countDeleted += (Integer) evt.getInfo();
        evt.setInfo(getEndMessage());
        if (stop || notifyProgressEnded) {
                listenerSupport.notifyEnded(evt);
        }
    }

    private Object getStartMessage(ProgressEvent evt) {
        return new MessageFormat(startMessage).format(new Object[]{evt.getMaximum()});
    }

    private Object getEndMessage() {
        return new MessageFormat(endMessage).format(new Object[]{countDeleted});
    }

    private void logDeleteRecords() {
        AppLogger.logInfo(DeleteOrphanedXmp.class, "DeleteOrphanedXmp.Info.StartRemove");
    }

    private void setMessagesFiles() {
        startMessage = JptBundle.INSTANCE.getString("DeleteOrphanedXmp.Files.Start");
        endMessage   = JptBundle.INSTANCE.getString("DeleteOrphanedXmp.Files.End");
    }

    private void setMessagesXmp() {
        startMessage = JptBundle.INSTANCE.getString("DeleteOrphanedXmp.Xmp.Start");
        endMessage   = JptBundle.INSTANCE.getString("DeleteOrphanedXmp.Xmp.End");
    }
}
