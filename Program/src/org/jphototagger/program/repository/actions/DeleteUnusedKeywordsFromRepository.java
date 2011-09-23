package org.jphototagger.program.repository.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.util.Bundle;

/**
 *
 * @author Elmar Baumann
 */
public final class DeleteUnusedKeywordsFromRepository implements Runnable, Cancelable {

    private volatile boolean cancel;
    private final ProgressListenerSupport ls = new ProgressListenerSupport();
    private volatile int countDeleted = 0;
    private static final Logger LOGGER = Logger.getLogger(DeleteUnusedKeywordsFromRepository.class.getName());
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
        ProgressEvent evt = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(count)
                .value(0)
                .info(getStartMessage())
                .build();

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
        ProgressEvent evt = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(count)
                .value(countDeleted)
                .info(keyword)
                .build();

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
        ProgressEvent evt = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(count)
                .value(countDeleted)
                .info(getEndMessage(count, countDeleted))
                .build();

        ls.notifyEnded(evt);
    }

    private Object getEndMessage(int count, int countDeleted) {
        return Bundle.getString(DeleteUnusedKeywordsFromRepository.class, "DeleteUnusedKeywords.Info.Finished", count, countDeleted);
    }

    private Object getStartMessage() {
        return Bundle.getString(DeleteUnusedKeywordsFromRepository.class, "DeleteUnusedKeywords.Info.Start");
    }
}
