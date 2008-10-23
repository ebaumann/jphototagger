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

    @Override
    public void run() {
        db.deleteNotExistingImageFiles(this);
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
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressPerformed(evt);
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressEnded(evt);
        }
    }
}
