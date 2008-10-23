package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class XmpUpdaterRenameInColumns implements Runnable, ProgressListener {

    private DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private List<String> filenames;
    private List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
    private Column column;
    private String oldValue;
    private String newValue;
    private boolean stop = false;

    public XmpUpdaterRenameInColumns(List<String> filenames, Column column,
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
