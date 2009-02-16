package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.Log;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.resource.Bundle;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Löscht in der Datenbank Datensätze mit Dateien, die nicht mehr existieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 * @see     Database#deleteNotExistingImageFiles(de.elmar_baumann.imv.event.ProgressListener)
 */
public final class RecordsWithNotExistingFilesDeleter implements Runnable,
    ProgressListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.getInstance();
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
            db.deleteNotExistingXmpData(this);
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
    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        evt.setInfo(getStartMessage(evt));
        evt.setSource(this);
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(evt);
            if (evt.isStop()) {
                stop = true; // stop = evt.isStop() can be wrong when more than 1 listener
            }
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        evt.setSource(this);
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
        evt.setSource(this);
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
        Log.logInfo(RecordsWithNotExistingFilesDeleter.class,
            Bundle.getString("RecordsWithNotExistingFilesDeleter.InformationMessage.StartRemove"));
    }

    private void setMessagesFiles() {
        startMessage = Bundle.getString("RecordsWithNotExistingFilesDeleter.Files.StartMessage");
        endMessage = Bundle.getString("RecordsWithNotExistingFilesDeleter.Files.EndMessage");
    }

    private void setMessagesXmp() {
        startMessage = Bundle.getString("RecordsWithNotExistingFilesDeleter.Xmp.StartMessage");
        endMessage = Bundle.getString("RecordsWithNotExistingFilesDeleter.Xmp.EndMessage");
    }
}
