package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Löscht in der Datenbank Datensätze mit Dateien, die nicht mehr existieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 * @see     Database#deleteNotExistingImageFiles(de.elmar_baumann.imv.event.ProgressListener)
 */
public class RecordsWithNotExistingFilesDeleter implements Runnable,
    ProgressListener {

    private DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
    private boolean notifyProgressEnded = false;
    private boolean stop = false;
    private int countDeleted = 0;

    @Override
    public void run() {
        db.deleteNotExistingImageFiles(this);
        if (!stop) {
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
        if (stop || notifyProgressEnded) {
            for (ProgressListener listener : progressListeners) {
                listener.progressEnded(evt);
            }
        }
    }
}
