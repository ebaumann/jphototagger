package org.jphototagger.program.repository.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * Deletes from the database keywords not contained in any image file.
 *
 * @author Elmar Baumann
 */
public final class DeleteUnusedKeywords implements Runnable, Cancelable {

    private volatile boolean cancel;
    private final ProgressListenerSupport ls = new ProgressListenerSupport();
    private volatile int countDeleted = 0;
    private static final Logger LOGGER = Logger.getLogger(DeleteUnusedKeywords.class.getName());
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

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
        List<String> keywords = new ArrayList<String>(repo.findNotReferencedDcSubjects());
        int size = keywords.size();

        notifyProgressStarted(size);

        for (int i = 0; !cancel && (i < size); i++) {
            String keyword = keywords.get(i);

            repo.deleteDcSubject(keyword);
            countDeleted++;
            notifyProgressPerformed(i + 1, countDeleted, keyword);
        }

        notifyProgressEnded(size, countDeleted);
    }

    @Override
    public void cancel() {
        cancel = true;
    }

    public void notifyProgressStarted(int count) {
        ProgressEvent evt = new ProgressEvent(this, 0, count, 0, getStartMessage());

        LOGGER.log(Level.INFO, "Deleting unused keywords");

        // Catching cancellation request
        for (ProgressListener listener : ls.get()) {
            listener.progressStarted(evt);

            if (evt.isCancel()) {

                // cancel = evt.isCancel() can be wrong when more than 1
                // listener
                cancel = true;
            }
        }
    }

    private void notifyProgressPerformed(int count, int countDeleted, String keyword) {
        ProgressEvent evt = new ProgressEvent(this, 0, count, countDeleted, keyword);

        // Catching cancellation request
        for (ProgressListener listener : ls.get()) {
            listener.progressPerformed(evt);

            if (evt.isCancel()) {

                // cancel = evt.isCancel() can be wrong when more than
                // 1 listener
                cancel = true;
            }
        }
    }

    public void notifyProgressEnded(int count, int countDeleted) {
        ProgressEvent evt = new ProgressEvent(this, 0, count, countDeleted, getEndMessage(count, countDeleted));

        ls.notifyEnded(evt);
    }

    private Object getEndMessage(int count, int countDeleted) {
        return Bundle.getString(DeleteUnusedKeywords.class, "DeleteUnusedKeywords.Info.Finished", count, countDeleted);
    }

    private Object getStartMessage() {
        return Bundle.getString(DeleteUnusedKeywords.class, "DeleteUnusedKeywords.Info.Start");
    }
}
