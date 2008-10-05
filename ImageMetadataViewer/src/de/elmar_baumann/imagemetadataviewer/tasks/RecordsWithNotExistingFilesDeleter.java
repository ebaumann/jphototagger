package de.elmar_baumann.imagemetadataviewer.tasks;

import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.event.ProgressEvent;
import de.elmar_baumann.imagemetadataviewer.event.ProgressListener;
import java.util.ArrayList;

/**
 * Löscht in der Datenbank Datensätze mit Dateien, die nicht mehr existieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/26
 * @see     Database#deleteNotExistingImageFiles(de.elmar_baumann.imagemetadataviewer.event.ProgressListener)
 */
public class RecordsWithNotExistingFilesDeleter implements Runnable,
    ProgressListener {

    private Database db = Database.getInstance();
    private ArrayList<ProgressListener> progressListeners = new ArrayList<ProgressListener>();

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

    /**
     * Entfernt einen Fortschrittsbeobachter.
     * 
     * @param listener Fortschrittsbeobachter
     */
    public void removeProgressListener(ProgressListener listener) {
        progressListeners.remove(listener);
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
