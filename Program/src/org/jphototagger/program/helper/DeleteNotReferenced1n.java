package org.jphototagger.program.helper;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.DatabaseMaintainance;
import org.jphototagger.program.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.resource.JptBundle;

/**
 * Deletes from the database records in 1:n tables not referenced by another
 * record.
 *
 * @author Elmar Baumann
 */
public final class DeleteNotReferenced1n implements Runnable {
    private final ProgressListenerSupport ls = new ProgressListenerSupport();
    private volatile int                  countDeleted = 0;

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
        countDeleted = DatabaseMaintainance.INSTANCE.deleteNotReferenced1n();
        notifyProgressEnded();
    }

    public void notifyProgressStarted() {
        ProgressEvent evt = new ProgressEvent(this, 0, 1, 0, getStartMessage());

        AppLogger.logInfo(DeleteNotReferenced1n.class,
                          "DeleteNotReferenced1n.Info.Start");
        ls.notifyStarted(evt);
    }

    public void notifyProgressEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, 1, 1, getEndMessage());

        ls.notifyEnded(evt);
    }

    private Object getStartMessage() {
        return JptBundle.INSTANCE.getString("DeleteNotReferenced1n.Info.Start");
    }

    private Object getEndMessage() {
        return JptBundle.INSTANCE.getString(
            "DeleteNotReferenced1n.Info.Finished", countDeleted);
    }
}
