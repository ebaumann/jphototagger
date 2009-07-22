package de.elmar_baumann.imv.helper;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.tasks.UserTasks;
import de.elmar_baumann.imv.view.panels.ProgressBarUserTasks;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JProgressBar;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class RenameXmpMetadata extends Thread
        implements ProgressListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final List<String> filenames;
    private final Column column;
    private final String oldValue;
    private final String newValue;
    private final ProgressBarUserTasks progressBarProvider =
            ProgressBarUserTasks.INSTANCE;
    private JProgressBar progressBar;

    public static synchronized void update(
            List<String> filenames,
            Column column,
            String oldValue,
            String newValue) {

        UserTasks.INSTANCE.add(new RenameXmpMetadata(
                filenames, column, oldValue, newValue));
    }

    private RenameXmpMetadata(List<String> filenames, Column column,
            String oldValue, String newValue) {
        this.filenames = new ArrayList<String>(filenames);
        this.column = column;
        this.oldValue = oldValue;
        this.newValue = newValue;
        setName("Renaming XMP metadata " + column + " @ " + // NOI18N
                getClass().getName());
    }

    @Override
    public void run() {
        logRename(column.getName(), oldValue, newValue);
        db.renameXmpMetadata(filenames, column, oldValue, newValue, this);
    }

    private void logRename(String columnName, String oldValue, String newValue) {
        AppLog.logInfo(RenameXmpMetadata.class, Bundle.getString(
                "RenameXmpMetadata.Info.StartRename", // NOI18N
                columnName, oldValue, newValue));
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        progressBar = (JProgressBar) progressBarProvider.getResource(this);
        if (progressBar != null) {
            progressBar.setMinimum(0);
            progressBar.setMaximum(evt.getMaximum());
            progressBar.setValue(0);
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setValue(evt.getValue());
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setValue(evt.getMaximum());
            progressBar = null;
            progressBarProvider.releaseResource(this);
        }
    }
}
