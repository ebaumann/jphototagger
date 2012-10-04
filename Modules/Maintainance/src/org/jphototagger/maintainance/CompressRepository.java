package org.jphototagger.maintainance;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.file.FilenameTokens;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.domain.repository.FileRepositoryProvider;
import org.jphototagger.domain.repository.RepositoryMaintainance;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class CompressRepository implements Runnable {

    private final ProgressListenerSupport listenerSupport = new ProgressListenerSupport();
    private boolean success = false;
    private long sizeBefore;
    private long sizeAfter;
    private static final Logger LOGGER = Logger.getLogger(CompressRepository.class.getName());

    public synchronized void addProgressListener(ProgressListener l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }

        listenerSupport.add(l);
    }

    public boolean getSuccess() {
        return success;
    }

    /**
     *
     * @return size in bytes
     */
    public long getSizeAfter() {
        return sizeAfter;
    }

    /**
     *
     * @return size in bytes
     */
    public long getSizeBefore() {
        return sizeBefore;
    }

    @Override
    public void run() {
        LOGGER.log(Level.INFO, "Compressing repository");
        notifyStarted();

        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);
        File dbFile = new File(provider.getFileRepositoryFileName(FilenameTokens.FULL_PATH));
        RepositoryMaintainance repo = Lookup.getDefault().lookup(RepositoryMaintainance.class);

        sizeBefore = dbFile.length();
        success = repo.compressRepository();
        sizeAfter = dbFile.length();
        notifyFinished();
        LOGGER.log(Level.INFO, "Compressing repository finished");
    }

    private synchronized void notifyStarted() {
        ProgressEvent evt = new ProgressEvent.Builder()
                .source(this)
                .info(Bundle.getString(CompressRepository.class, "CompressRepository.Start"))
                .build();

        listenerSupport.notifyStarted(evt);
    }

    private synchronized void notifyFinished() {
        ProgressEvent evt = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(1)
                .value(1)
                .info(getEndMessage())
                .build();

        listenerSupport.notifyEnded(evt);
    }

    private Object getEndMessage() {
        double mb = 1024 * 1024;
        Object[] params = {success
            ? Bundle.getString(CompressRepository.class, "CompressRepository.End.Success.True")
            : Bundle.getString(CompressRepository.class, "CompressRepository.End.Success.False"), sizeBefore,
            new Double(sizeBefore / mb), sizeAfter, new Double(sizeAfter / mb)};

        return Bundle.getString(CompressRepository.class, "CompressRepository.End", params);
    }
}
