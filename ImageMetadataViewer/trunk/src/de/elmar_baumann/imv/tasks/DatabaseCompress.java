package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseMaintainance;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.resource.Bundle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Compresses the database.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/30
 */
public final class DatabaseCompress implements Runnable {

    private final List<ProgressListener> listeners =
            new ArrayList<ProgressListener>();
    private boolean success = false;
    private long sizeBefore;
    private long sizeAfter;

    public synchronized void addProgressListener(ProgressListener l) {
        listeners.add(l);
    }

    /**
     * Returns the success of the database operation.
     * 
     * @return true if successfully compressed.
     */
    public boolean getSuccess() {
        return success;
    }

    /**
     * Returns the size of the database file in bytes before compressing.
     *
     * <em>The value is (only) valid after compressing the database file!</em>
     *
     * @return size in bytes
     */
    public long getSizeAfter() {
        return sizeAfter;
    }

    /**
     * Returns the size of the database file in bytes after compressing.
     *
     * <em>The value is (only) valid after compressing the database file!</em>
     *
     * @return size in bytes
     */
    public long getSizeBefore() {
        return sizeBefore;
    }

    /**
     * Compresses the database. The process can't be stopped and the progress
     * values can't submitted to them. The listeners are only notified about
     * the start and end of compressing.
     */
    @Override
    public void run() {
        logCompressDatabase();
        notifyStarted();
        File dbFile = new File(UserSettings.INSTANCE.getDatabaseFileName(true));
        sizeBefore = dbFile.length();
        success = DatabaseMaintainance.INSTANCE.compressDatabase();
        sizeAfter = dbFile.length();
        notifyEnded();
    }

    private synchronized void notifyStarted() {
        ProgressEvent evt = new ProgressEvent(this,
                Bundle.getString("DatabaseCompress.StartMessage")); // NOI18N
        for (ProgressListener listener : listeners) {
            listener.progressStarted(evt);
        }
    }

    private void logCompressDatabase() {
        AppLog.logInfo(DatabaseCompress.class, Bundle.getString(
                "DatabaseCompress.InformationMessage.StartCompress")); // NOI18N
    }

    private synchronized void notifyEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, 1, 1, getEndMessage());
        for (ProgressListener listener : listeners) {
            listener.progressEnded(evt);
        }
    }

    private Object getEndMessage() {
        double mb = 1024 * 1024;
        Object[] params = {Bundle.getString(
            success
            ? "DatabaseCompress.EndMessage.Success.True" // NOI18N
            : "DatabaseCompress.EndMessage.Success.False"), // NOI18N
            sizeBefore, new Double(sizeBefore / mb), sizeAfter,
            new Double(sizeAfter / mb)
        };
        return Bundle.getString("DatabaseCompress.EndMessage", params); // NOI18N
    }
}
