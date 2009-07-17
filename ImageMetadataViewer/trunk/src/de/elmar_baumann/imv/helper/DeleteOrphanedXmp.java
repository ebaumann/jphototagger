package de.elmar_baumann.imv.helper;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.resource.Bundle;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Löscht in der Datenbank Datensätze mit Dateien, die nicht mehr existieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008/10/05
 * @see     DatabaseImageFiles#deleteOrphanedXmp(de.elmar_baumann.imv.event.listener.ProgressListener)
 */
public final class DeleteOrphanedXmp
        implements Runnable, ProgressListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
    private boolean notifyProgressEnded = false;
    private String startMessage;
    private String endMessage;
    private boolean stop = false;
    private int countDeleted = 0;

    @Override
    public void run() {
        setMessagesFiles();
        logDeleteRecords();
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
        progressListeners.add(listener);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        evt.setInfo(getStartMessage(evt));
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(evt);
            if (evt.isStop()) {
                stop = true; // stop = evt.isStop() can be wrong when more than 1 listener
            }
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressPerformed(evt);
            if (evt.isStop()) {
                stop = true; // stop = evt.isStop() can be wrong when more than 1 listener
            }
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        countDeleted += (Integer) evt.getInfo();
        evt.setInfo(getEndMessage());
        if (stop || notifyProgressEnded) {
            for (ProgressListener listener : progressListeners) {
                listener.progressEnded(evt);
            }
        }
    }

    private Object getStartMessage(ProgressEvent evt) {
        return new MessageFormat(startMessage).format(new Object[]{evt.getMaximum()});
    }

    private Object getEndMessage() {
        return new MessageFormat(endMessage).format(new Object[]{countDeleted});
    }

    private void logDeleteRecords() {
        AppLog.logInfo(DeleteOrphanedXmp.class,
            Bundle.getString("RecordsWithNotExistingFilesDeleter.Info.StartRemove")); // NOI18N
    }

    private void setMessagesFiles() {
        startMessage = Bundle.getString("RecordsWithNotExistingFilesDeleter.Files.Start"); // NOI18N
        endMessage = Bundle.getString("RecordsWithNotExistingFilesDeleter.Files.End"); // NOI18N
    }

    private void setMessagesXmp() {
        startMessage = Bundle.getString("RecordsWithNotExistingFilesDeleter.Xmp.Start"); // NOI18N
        endMessage = Bundle.getString("RecordsWithNotExistingFilesDeleter.Xmp.End"); // NOI18N
    }
}
