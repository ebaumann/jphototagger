package org.jphototagger.program.module.maintainance;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.domain.repository.RepositoryMaintainance;
import org.jphototagger.lib.util.Bundle;

/**
 * Deletes from the repository records in 1:n not referenced by another record.
 *
 * @author Elmar Baumann
 */
public final class DeleteNotReferenced1n implements Runnable {

    private final ProgressListenerSupport ls = new ProgressListenerSupport();
    private volatile int countDeleted = 0;
    private static final Logger LOGGER = Logger.getLogger(DeleteNotReferenced1n.class.getName());

    public synchronized void addProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    public synchronized int getCountDeleted() {
        return countDeleted;
    }

    @Override
    public void run() {
        notifyProgressStarted();
        RepositoryMaintainance repo = Lookup.getDefault().lookup(RepositoryMaintainance.class);
        countDeleted = repo.deleteNotReferenced1n();
        notifyProgressEnded();
    }

    public void notifyProgressStarted() {
        ProgressEvent evt = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(1)
                .value(0)
                .info(getStartMessage())
                .build();

        LOGGER.log(Level.INFO, "Deleting not referenced data from the repository");
        ls.notifyStarted(evt);
    }

    public void notifyProgressEnded() {
        ProgressEvent evt = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(1)
                .value(1)
                .info(getEndMessage())
                .build();

        ls.notifyEnded(evt);
    }

    private Object getStartMessage() {
        return Bundle.getString(DeleteNotReferenced1n.class, "DeleteNotReferenced1n.Info.Start");
    }

    private Object getEndMessage() {
        return Bundle.getString(DeleteNotReferenced1n.class, "DeleteNotReferenced1n.Info.Finished", countDeleted);
    }
}
