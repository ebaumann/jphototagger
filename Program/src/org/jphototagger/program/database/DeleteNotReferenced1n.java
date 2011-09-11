package org.jphototagger.program.database;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.api.event.ProgressEvent;
import org.jphototagger.api.event.ProgressListener;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.domain.repository.RepositoryMaintainance;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * Deletes from the database records in 1:n tables not referenced by another
 * record.
 *
 * @author Elmar Baumann
 */
final class DeleteNotReferenced1n implements Runnable {

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
        ProgressEvent evt = new ProgressEvent(this, 0, 1, 0, getStartMessage());

        LOGGER.log(Level.INFO, "Deleting not referenced data from the database");
        ls.notifyStarted(evt);
    }

    public void notifyProgressEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, 1, 1, getEndMessage());

        ls.notifyEnded(evt);
    }

    private Object getStartMessage() {
        return Bundle.getString(DeleteNotReferenced1n.class, "DeleteNotReferenced1n.Info.Start");
    }

    private Object getEndMessage() {
        return Bundle.getString(DeleteNotReferenced1n.class, "DeleteNotReferenced1n.Info.Finished", countDeleted);
    }
}
