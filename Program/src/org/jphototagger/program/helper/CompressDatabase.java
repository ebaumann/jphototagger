package org.jphototagger.program.helper;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.api.core.UserFilesProvider;
import org.jphototagger.domain.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.lib.event.ProgressEvent;
import org.jphototagger.lib.event.listener.ProgressListener;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.database.DatabaseMaintainance;
import org.jphototagger.api.file.Filename;
import org.openide.util.Lookup;

/**
 * Compresses the database.
 *
 * @author Elmar Baumann
 */
public final class CompressDatabase implements Runnable {

    private final ProgressListenerSupport listenerSupport = new ProgressListenerSupport();
    private boolean success = false;
    private long sizeBefore;
    private long sizeAfter;
    private static final Logger LOGGER = Logger.getLogger(CompressDatabase.class.getName());

    public synchronized void addProgressListener(ProgressListener l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }

        listenerSupport.add(l);
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
     * Compresses the database. The process can't be cancelled and the progress
     * values can't submitted to them. The listeners are only notified about
     * the start and end of compressing.
     */
    @Override
    public void run() {
        logCompressDatabase();
        notifyStarted();

        UserFilesProvider provider = Lookup.getDefault().lookup(UserFilesProvider.class);
        File dbFile = new File(provider.getDatabaseFileName(Filename.FULL_PATH));

        sizeBefore = dbFile.length();
        success = DatabaseMaintainance.INSTANCE.compressDatabase();
        sizeAfter = dbFile.length();
        notifyEnded();
    }

    private synchronized void notifyStarted() {
        ProgressEvent evt = new ProgressEvent(this, Bundle.getString(CompressDatabase.class, "CompressDatabase.Start"));

        listenerSupport.notifyStarted(evt);
    }

    private void logCompressDatabase() {
        LOGGER.log(Level.INFO, "Compressing database");
    }

    private synchronized void notifyEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, 1, 1, getEndMessage());

        listenerSupport.notifyEnded(evt);
    }

    private Object getEndMessage() {
        double mb = 1024 * 1024;
        Object[] params = {success
            ? Bundle.getString(CompressDatabase.class, "CompressDatabase.End.Success.True")
            : Bundle.getString(CompressDatabase.class, "CompressDatabase.End.Success.False"), sizeBefore,
            new Double(sizeBefore / mb), sizeAfter, new Double(sizeAfter / mb)};

        return Bundle.getString(CompressDatabase.class, "CompressDatabase.End", params);
    }
}
