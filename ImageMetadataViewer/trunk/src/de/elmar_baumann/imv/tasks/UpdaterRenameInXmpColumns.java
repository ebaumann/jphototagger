package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.Log;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.resource.Bundle;
import java.text.MessageFormat;
import java.util.List;
import java.util.ArrayList;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class UpdaterRenameInXmpColumns implements Runnable, ProgressListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private final List<String> filenames;
    private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
    private final Column column;
    private final String oldValue;
    private final String newValue;
    private boolean stop = false;

    public UpdaterRenameInXmpColumns(List<String> filenames, Column column,
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
        logRename(column.getName(), oldValue, newValue);
        db.renameInXmpColumns(filenames, column, oldValue, newValue, this);
    }

    private void checkStopEvent(ProgressEvent evt) {
        if (stop) {
            evt.stop();
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        checkStopEvent(evt);
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(evt);
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        checkStopEvent(evt);
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

    private void logRename(String columnName, String oldValue, String newValue) {
            MessageFormat msg = new MessageFormat(
                Bundle.getString("UpdaterRenameInXmpColumns.InformationMessage.StartRename"));
            Object[] params = {columnName, oldValue, newValue};
            Log.logInfo(UpdaterRenameInXmpColumns.class, msg.format(params));
    }
}
