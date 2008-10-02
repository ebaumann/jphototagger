package de.elmar_baumann.imagemetadataviewer.tasks;

import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.event.ProgressEvent;
import de.elmar_baumann.imagemetadataviewer.event.ProgressListener;
import java.util.Vector;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public class XmpUpdaterRenameInColumns implements Runnable, ProgressListener {

    private Database db = Database.getInstance();
    private Vector<String> filenames;
    private Vector<ProgressListener> progressListeners = new Vector<ProgressListener>();
    private Column column;
    private String oldValue;
    private String newValue;
    private boolean stop = false;

    public XmpUpdaterRenameInColumns(Vector<String> filenames, Column column,
        String oldValue, String newValue) {
        this.filenames = filenames;
        this.column = column;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Fügt einen Fortschrittsbeobachter hinzu.
     * 
     * @param listener Beobachter
     */
    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    /**
     * Entfernt einen Fortschrittsbeobachter.
     * 
     * @param listener Beobachter
     */
    public void removeProgressListener(ProgressListener listener) {
        progressListeners.remove(listener);
    }

    /**
     * Unterbricht die Arbeit.
     */
    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        db.renameInXmpColumns(filenames, column, oldValue, newValue, this);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        evt.setStop(stop);
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(evt);
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        evt.setStop(stop);
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
